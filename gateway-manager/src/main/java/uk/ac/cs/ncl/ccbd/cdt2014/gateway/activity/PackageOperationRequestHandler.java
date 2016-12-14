package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.concurrent.CountDownLatch;

/**
 * Implementation of client's package related requests. Supports three operations:
 * 1. Retrieving all packages currently deployed in a remote device
 * 2. Install a deployment package in a remote device
 * 3. Uninstall a deployment package from a remote device 
 * @author Saleh Mohamed
 * @version April 2016
 */
public class PackageOperationRequestHandler extends RequestHandlerImpl{
	protected String deployUrl;
	protected String packageName;

	public PackageOperationRequestHandler(String deviceId, String broker, String action, String method, String resource, String deployUrl, String packageName) {
		super(deviceId, broker, action, method, resource);
		this.deployUrl = deployUrl;
		this.packageName = packageName;
		
		if ("install".equals(resource))
			setPayloadMetrics("deploy.url", this.deployUrl);
		if ("uninstall".equals(resource))
			setPayloadMetrics("deploy.pkg.name", this.packageName);
	}
	
	@Override
	public void makeRequest(CountDownLatch latch) throws Exception {
		if ("install".equals(resource) || "packages".equals(resource))
			super.makeRequest(latch);
		if ("uninstall".equals(resource)) {
			Publisher publisher = new Publisher(mqttClient, getPayloadMetrics(), requestTopic.toString(), packageName, latch);
			publisher.start();
		}
	}
	
	@Override
	public void getResponse(CountDownLatch latch) {
		super.getResponse(latch);		
	}
}
