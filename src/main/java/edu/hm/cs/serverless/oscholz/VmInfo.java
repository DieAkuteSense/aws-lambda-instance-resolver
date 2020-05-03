package edu.hm.cs.serverless.oscholz;

/**
 * Object to hold IDs
 *
 * @author Oliver Scholz
 */
public class VmInfo {
	private String vmId;
	private String instId;
	private String uptime;

	public String getVmId() {
		return vmId;
	}

	public void setVmId(String vmId) {
		this.vmId = vmId;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}
}
