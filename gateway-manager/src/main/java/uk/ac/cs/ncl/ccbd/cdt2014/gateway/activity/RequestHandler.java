package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.concurrent.CountDownLatch;

public interface RequestHandler {
	/**
	 * Send request REST request to a gateway device through MQTT server. The class implementing this 
	 * interface specifies the type of request supported (GET, PUT, DEL or EXEC).
	 * @param latch
	 * @throws Exception
	 */
	public void makeRequest(CountDownLatch latch) throws Exception;
	
	/**
	 * Allows client to subscribe to a response  topic in the MQTT server. Once the response is receive
	 * the client needs to unsubscribe from the response topic.
	 * @param latch
	 */
	public void getResponse(CountDownLatch latch);
}
