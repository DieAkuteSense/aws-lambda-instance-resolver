package edu.hm.cs.serverless.oscholz;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
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
	public Object handleRequest(final Object input, final Context context) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("X-Custom-Header", "application/json");

		VmInfo vmInfo = new VmInfo();
		gatherInstanceInfo(vmInfo);
		gatherUptime(vmInfo);

		Map<String, String> osRelease = new HashMap<>();
		gatherOsReleaseInfo(osRelease);

		// LambdaLogger logger = context.getLogger();
		// Files.list(new File("/etc").toPath()).forEach(p -> logger.log(p +"\n"));

		try {
			Gson gson = new Gson();
			final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
			String output = String.format("{ \"location\": \"%s\", \"vm\": %s, \"os-release\": %s }", pageContents, gson.toJson(vmInfo), gson.toJson(osRelease));

			ResponseBody body = new ResponseBody(vmInfo, osRelease, pageContents);

			return new GatewayResponse(body, headers, 200);
		} catch (IOException e) {
			return new GatewayResponse(null, headers, 500);
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
}
