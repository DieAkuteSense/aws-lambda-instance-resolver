package edu.hm.cs.serverless.oscholz;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO containing response object for API Gateway.
 */
public class GatewayResponse {

	private final ResponseBody body;
	private final Map<String, String> headers;
	private final int statusCode;

	public GatewayResponse(final ResponseBody body, final Map<String, String> headers, final int statusCode) {
		this.statusCode = statusCode;
		this.body = body;
		this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
	}

	public ResponseBody getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
