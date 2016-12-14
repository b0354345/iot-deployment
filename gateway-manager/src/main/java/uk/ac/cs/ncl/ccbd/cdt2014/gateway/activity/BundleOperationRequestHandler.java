package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.concurrent.CountDownLatch;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.MissingArgumentException;

/**
 * Implementation of client's bundle related requests. Supports three operations:
 * 1. Retrieving all the bundles (applications) currently installed in a remote device
 * 2. Start a bundle in a remote device
 * 3. Stop a bundle running in a remote device 
 * @author Saleh Mohamed
 * @version April 2016
 */
public class BundleOperationRequestHandler extends RequestHandlerImpl {

	public BundleOperationRequestHandler(String deviceId, String broker, String action, String method, String resource) {
		super(deviceId, broker, action, method, resource);
	}

	@Override
	public void makeRequest(CountDownLatch latch) throws Exception {
		String[] tokens = resource.split("/");
		if ("start".equals(tokens[0]) || "stop".equals(tokens[0])) {
			if (tokens.length < 2)
				throw new MissingArgumentException("Missing application id");
			try {
				Integer.parseInt(tokens[1]);
			} catch (NumberFormatException e) {
				e.printStackTrace();;
				System.exit(1);
			} 
		}
		super.makeRequest(latch);
	}
	
	@Override
	public void getResponse(CountDownLatch latch) {
		super.getResponse(latch);		
	}
}
