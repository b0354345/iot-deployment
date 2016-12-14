package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.concurrent.CountDownLatch;

public class DelRequestHandler extends RequestHandlerImpl{

	public DelRequestHandler(String deviceId, String broker, String action, String method, String resource) {
		super(deviceId, broker, action, method, resource);
	}

	@Override
	public void makeRequest(CountDownLatch latch) {

	}

	@Override
	public void getResponse(CountDownLatch latch) {

	}
}
