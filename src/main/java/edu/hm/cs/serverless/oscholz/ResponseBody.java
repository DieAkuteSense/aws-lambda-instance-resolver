package edu.hm.cs.serverless.oscholz;

import java.util.Map;

/**
 * Hold the response body as object
 *
 * @author Oliver Scholz
 */
public class ResponseBody {

	private VmInfo vmInfo;
	private Map<String, String> osRelease;
	private String location;
	private Map<String, String> cmdResults;
	private Map<String, String[]> directories;

	public ResponseBody(
			VmInfo vmInfo,
			Map<String, String> osRelease,
			String location,
			Map<String, String> cmdResults,
			Map<String, String[]> directories
		) {
		this.vmInfo = vmInfo;
		this.osRelease = osRelease;
		this.location = location;
		this.cmdResults = cmdResults;
		this.directories = directories;
	}

	public VmInfo getVmInfo() {
		return vmInfo;
	}

	public void setVmInfo(VmInfo vmInfo) {
		this.vmInfo = vmInfo;
	}

	public Map<String, String> getOsRelease() {
		return osRelease;
	}

	public void setOsRelease(Map<String, String> osRelease) {
		this.osRelease = osRelease;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Map<String, String> getCmdResults() {
		return cmdResults;
	}

	public void setCmdResults(Map<String, String> cmdResults) {
		this.cmdResults = cmdResults;
	}

	public Map<String, String[]> getDirectories() {
		return directories;
	}

	public void setDirectories(Map<String, String[]> directories) {
		this.directories = directories;
	}
}
