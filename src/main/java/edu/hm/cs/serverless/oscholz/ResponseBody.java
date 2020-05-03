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

	public ResponseBody(VmInfo vmInfo, Map<String, String> osRelease, String location) {
		this.vmInfo = vmInfo;
		this.osRelease = osRelease;
		this.location = location;
	}
}
