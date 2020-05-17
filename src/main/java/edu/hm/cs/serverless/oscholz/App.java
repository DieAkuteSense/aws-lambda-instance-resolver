package edu.hm.cs.serverless.oscholz;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.commons.lang3.SystemUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple Request Handler to return information about Lambda-Instance
 *
 * @author Oliver Scholz
 */
public class App implements RequestHandler<Object, Object> {

	/**
	 * Handling request accesses cgroup information of process and attaches some information as VM ID and Instance ID
	 *
	 * @param input   Lambda Request Input
	 * @param context Lambda Context
	 * @return JSON containing VM and Instance ID
	 */
	public GatewayResponse handleRequest(final Object input, final Context context) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("X-Custom-Header", "application/json");

		writeFileToTemp();

		VmInfo vmInfo = new VmInfo();
		gatherInstanceInfo(vmInfo);
		gatherUptime(vmInfo);
		gatherCpuModelName(vmInfo);

		Map<String, String> osRelease = new HashMap<>();
		gatherOsReleaseInfo(osRelease);

		Map<String, String[]> directories = new HashMap<>();
		listDirectory("/tmp", directories);

		Map<String, String> cmdResults = new HashMap<>();
		cmdResults.put("kernel_version", execShell("uname -r"));
		cmdResults.put("hostname", execShell("uname -n"));

		try {
			final String pageContents = this.getPageContents("https://checkip.amazonaws.com");

			ResponseBody body = new ResponseBody(vmInfo, osRelease, pageContents, cmdResults, directories);
			return new GatewayResponse(body, headers, 200);
		} catch (IOException e) {
			// return new GatewayResponse(null, headers, 500);
			return null;
		}
	}

	private String getPageContents(String address) throws IOException {
		URL url = new URL(address);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}

	private static void gatherInstanceInfo(VmInfo vmInfo) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/proc/self/cgroup"));
			String line = br.readLine();


			while (line != null) {
				if (line.contains(":cpu:")) {
					String[] info = line.split(":")[2].split("/");
					vmInfo.setVmId(info[1]);
					vmInfo.setInstId(info[2]);
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			// currently ignore this
		}
	}

	private static void gatherOsReleaseInfo(Map<String, String> osRelease) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/etc/os-release"));
			String line = br.readLine();

			while (line != null) {
				String[] info = line.split("=");
				String val = info[1].substring(1, info[1].length() - 1);
				osRelease.put(info[0], val);
				line = br.readLine();
			}
		} catch (IOException e) {
			// currently ignore
		}
	}

	private static void gatherUptime(VmInfo vmInfo) {
		try {
			BufferedReader brUptime = new BufferedReader(new FileReader("/proc/uptime"));
			String uptime = brUptime.readLine();

			if (uptime != null) {
				vmInfo.setUptime(uptime.split(" ")[0]);
			}
		} catch (IOException e) {
			// currently ignore
		}
	}

	private static void gatherCpuModelName(VmInfo vmInfo) {
		try {
			BufferedReader brUptime = new BufferedReader(new FileReader("/proc/cpuinfo"));
			String s = "";
			while ((s = brUptime.readLine()) != null) {
				if (s.startsWith("model name")) {
					vmInfo.setCpuModel(s.split(":")[1].trim());
				}
			}
		} catch (IOException e) {
			// currently ignore
		}
	}

	private static void writeFileToTemp() {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

		execShell("touch /tmp/" + date.format(dateTimeFormatter));
	}

	private static void listDirectory(String directory, Map<String, String[]> directoies) {
		try {
			List<Path> files = Files.list(new File(directory).toPath()).collect(Collectors.toList());
			String[] s = new String[files.size()];
			for (int i = 0; i < files.size(); i++) {
				s[i] = files.get(i).getFileName().toString();
			}
			directoies.put(directory, s);
		} catch (IOException e) {
			// currentiry ignore
		}
	}

	private static String execShell(String cmd) {
		StringBuilder sb = new StringBuilder();
		if (SystemUtils.IS_OS_LINUX) {
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String s;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				p.waitFor();
				System.out.println ("exit: " + p.exitValue());
				p.destroy();
			} catch (IOException | InterruptedException e) {
				// currently ignore
			}
		}
		return sb.toString();
	}
}
