package edu.hm.cs.serverless.oscholz;

import java.util.Map;

/**
 * Created by Olli on 03.05.2020.
 * Package edu.hm.cs.serverless.oscholz
 * Project AWS Lambda
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
