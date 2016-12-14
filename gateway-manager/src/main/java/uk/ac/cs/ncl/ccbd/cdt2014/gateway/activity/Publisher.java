package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.mqttclient.IKuraMQTTClient;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.payload.KuraPayload;

/**
 * Publisher class - makes use of mqtt connection created by KuraMQTTClient to publish and a 
 * request message to mqtt server.
 * @author Saleh Mohamed 
 * @version March 2016
 */
public class Publisher extends Thread{
	private final Logger logger = LoggerFactory.getLogger(Publisher.class);
	private final CountDownLatch latch;
	private IKuraMQTTClient mqttClient;
	private String requestTopic;
	private String payloadBody;
	private Map<String, String> payloadMetrics;
	
	public Publisher(IKuraMQTTClient mqttClient, Map<String,String> payloadMetrics, String requestTopic, String payloadBody, CountDownLatch latch) {
		this.mqttClient = mqttClient;
		this.payloadMetrics = payloadMetrics;
		this.requestTopic = requestTopic;
		this.latch = latch;
		this.payloadBody = payloadBody;
	}
	private void publishMassage() {
		if (mqttClient == null) {
			logger.debug("Communication Problem", "Something bad happened to the connection");
			return;
		}
		
		if (!mqttClient.isConnected()) {
			logger.debug("Error: Can not publish to the server, mqtt client is not connected");
			return;
		}
		
		if ((mqttClient.isConnected()) && ((requestTopic == null)
				|| "".equals(requestTopic))) {
			logger.debug("Error while publishing", "No valid topic name supplied");
			return;
		}
		
		if ((payloadMetrics.get("request.id") == null) || "".equals("request.id")) {
			logger.debug("Error in Publishing", "A request id must be supplied");
			return;
		}
		
		if ((payloadMetrics.get("requester.client.id") == null) || "".equals("requester.client.id")) {
			logger.debug("Error in Publishing", "Requester client id must be supplied");
			return;
		}
		
		KuraPayload payload = new KuraPayload();
		addPayloadMetrics(payload);
		if (payloadBody != null)
			payload.setBody(payloadBody.getBytes());
		logger.debug("Publishing on topic \"{}\"", requestTopic);
		mqttClient.publish(requestTopic, payload);
		logger.info("Request \"{}\" successfully sent", payloadMetrics.get("request.id"));
		latch.countDown();
		logger.info("Publisher thread terminating");
	}
	
	/**
	 * Add metrics to the payload
	 * @param payload
	 */
	public void addPayloadMetrics(KuraPayload payload) {
		for (String str : payloadMetrics.keySet()) {
			payload.addMetric(str, payloadMetrics.get(str));
		}
	}
	
	@Override
	public void run() {
		publishMassage();		
	}
	
}