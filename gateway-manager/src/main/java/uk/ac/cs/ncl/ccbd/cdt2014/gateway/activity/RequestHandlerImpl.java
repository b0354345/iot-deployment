package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.deploymentListener.GatewayServerSocket;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.mqttclient.IKuraMQTTClient;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.mqttclient.KuraMQTTClient;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.PayloadUtil;

/**
 * A concrete implementation of RequestHandler interface.
 * @author Saleh Mohamed <s.mohamed@ncl.ac.uk>
 * @version April 2016
 */
public class RequestHandlerImpl implements RequestHandler, ResponseListener {
	// protected String broker;
	protected String clientId;
	protected String requestId;
	// protected String action;
	protected String method;
	protected String resource;
	protected StringBuilder requestTopic;
	protected String responseTopic;
	protected IKuraMQTTClient mqttClient;
	protected Map<String, String> payloadMetrics;

	public RequestHandlerImpl(String deviceId, String broker, String action, String method, String resource) {
		this.method = method;
		this.resource = resource;
		clientId = PayloadUtil.generateClientId();
		requestId = PayloadUtil.generateRequestId();
		requestTopic = new StringBuilder("$EDC/cebren/");
		requestTopic.append(deviceId + "/");
		requestTopic.append(action + "/");
		requestTopic.append(method + "/");
		requestTopic.append(resource);
		responseTopic = PayloadUtil.generateHintSubscriptionTopic(requestTopic.toString(), requestId, clientId);
		mqttClient = new KuraMQTTClient(broker, "1883", "local", "admin", "admin");
		mqttClient.connect();
		payloadMetrics = new HashMap<String, String>();
		payloadMetrics();
	}
	
	public IKuraMQTTClient getMQQTClient() {
		return mqttClient;
	}

	public void makeRequest(CountDownLatch latch) throws Exception {
		Publisher publisher = new Publisher(mqttClient, payloadMetrics, requestTopic.toString(), null, latch);
		publisher.start();

	}

	public void getResponse(CountDownLatch latch) {
		Subscriber subscriber = new Subscriber(mqttClient, responseTopic, latch);
		subscriber.addListener(this);
		subscriber.start();
	}

	/**
	 * Set payload metrics depending on the type of resource requested
	 */
	public void payloadMetrics() {
		payloadMetrics.put("request.id", requestId);
		payloadMetrics.put("requester.client.id", clientId);
	}

	public Map<String, String> getPayloadMetrics() {
		return payloadMetrics;
	}

	public void setPayloadMetrics(String key, String value) {
		this.payloadMetrics.put(key, value);
	}

	@Override
	public void onMqttResponseArrived(String message) throws IOException {
		OutputStreamWriter osw = GatewayServerSocket.getOsw();
		StringBuffer sb = new StringBuffer(message);
		osw.write(sb.toString());
		osw.flush();
		osw.write("EOF");
		osw.flush();
		osw.close();
		
		
		//osw.close();
	}
}
