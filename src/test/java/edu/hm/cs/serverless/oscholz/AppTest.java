package edu.hm.cs.serverless.oscholz;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {

  @Test
  public void successfulResponse() {
    App app = new App();
    GatewayResponse result = app.handleRequest(null, null);
    //assertEquals(result.getStatusCode(), 200);
    //assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    //ResponseBody content = result.getBody();
    assertNotNull(result);
    System.out.println(result.getBody().getVmInfo().toString());
  }
}
