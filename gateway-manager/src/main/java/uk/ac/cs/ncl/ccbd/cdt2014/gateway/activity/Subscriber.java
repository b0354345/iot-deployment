package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.mqttclient.IKuraMQTTClient;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.mqttclient.MessageListener;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.payload.KuraPayload;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.PayloadUtil;

/**
 * Subscriber class - makes use of mqtt connection created by KuraMQTTClient to subscribe and receive a 
 * reply message from mqtt server.
 * @author Saleh Mohamed 
 * @version March 2016
 */
public class Subscriber extends Thread{
	private final Logger logger = LoggerFactory.getLogger(Subscriber.class);
	private List<ResponseListener> listeners;
	private final CountDownLatch latch;
	private IKuraMQTTClient mqttClient;
	private String responseTopic;
	private String responsePayload;
	
	public Subscriber(IKuraMQTTClient mqttClient, String responseTopic, CountDownLatch latch) {
		this.mqttClient = mqttClient;
		this.responseTopic = responseTopic;
		this.latch = latch;
		this.responsePayload = "";
		listeners = new ArrayList<ResponseListener>();
	}
	
	public void addListener(ResponseListener listener) {
		listeners.add(listener);
	}
	
	private void createSubscription() {
		if (mqttClient == null) {
			logger.debug("Communication Problem", "Something bad happened to the connection");
			return;
		}
		
		if (!mqttClient.isConnected()) {
			logger.debug("Error: Can not subscribe to the server, mqtt client is not connected");
			return;
		}
		
		if ((mqttClient.isConnected()) && ((responseTopic == null)
				|| "".equals(responseTopic))) {
			logger.debug("Error while Subscribing", "A valid topic name must be supplied");
			return;
		}
		
		logger.debug("Subscribing to the topic \"{}\"... ", responseTopic);
		mqttClient.subscribe(responseTopic, new MessageListener() {
			@Override
			public void processMessage(final KuraPayload payload) {
				logger.debug("Message Received");
				final StringBuilder responseBuilder = new StringBuilder();
				try {
					responseBuilder.append(PayloadUtil.parsePayloadFromProto(payload.metrics())).append("\n")
							.append((payload.getBody() != null) ? new String(payload.getBody(), "UTF-8") : "");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				responsePayload = responseBuilder.toString();
				System.out.println(responsePayload);
				logger.debug("Unsubscribing to the topic \"{}\"... ", responseTopic);
				mqttClient.unsubscribe(responseTopic);
				latch.countDown();
				logger.debug("Subscriber thread terminating");
				
				// listeners
				for (ResponseListener listener : listeners) {
					try {
						logger.debug("GATEWAY MANAGER: Sending response to the deployment handler");
						listener.onMqttResponseArrived(responsePayload);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});	
	}

	public String getResponsePayload() {
		return responsePayload;
	}

	@Override
	public void run() {
		createSubscription();
		
	}
}
