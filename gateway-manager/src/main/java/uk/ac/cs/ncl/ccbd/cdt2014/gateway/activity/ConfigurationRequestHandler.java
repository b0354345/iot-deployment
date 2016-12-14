package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.MissingArgumentException;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.XmlUtil;

/**
 * Implementation of client's configuration related requests. Supports three operations:
 * 1. Retrieving current configurations of all deployed applications in a remote device
 * 2. Retrieve current configuration of a given application
 * 3. Updating a configurable property of a given application 
 * @author Saleh Mohamed
 * @version April 2016
 */
public class ConfigurationRequestHandler extends RequestHandlerImpl{
	private final Logger logger = LoggerFactory.getLogger(ConfigurationRequestHandler.class);
	
	private List<String> properties;

	public ConfigurationRequestHandler(String deviceId, String broker, String action, String method, String resource, List<String> properties) {
		super(deviceId, broker, action, method, resource);
		this.properties = properties;
	}
	
	@Override
	public void makeRequest(CountDownLatch latch) throws Exception {
		if ("GET".equals(method))
			super.makeRequest(latch);
		
		if ("PUT".equals(method)) {
			
			if (properties == null)
				throw new MissingArgumentException("\"property\" can not be null for PUT request");
			
			// Send get request to receive xml configuration file for the app
			String getTopic = requestTopic.toString().replace("PUT", "GET");
			CountDownLatch getLatch = new CountDownLatch(2);
			
			// Send GET request first
			Publisher publisher = new Publisher(mqttClient, getPayloadMetrics(), getTopic, null, getLatch);
			publisher.start();
			
			// Subscribe for the GET response
			Subscriber subscriber = new Subscriber(mqttClient, responseTopic, getLatch); 
			subscriber.start();
			getLatch.await();
			String resp = subscriber.getResponsePayload();
			//logger.debug("Current configurations: \n" + resp);
			
			// reset the connection
			this.getMQQTClient().disconnect();
			this.getMQQTClient().connect();
			
			// process and update the configuration file
			logger.debug("Processing configuration file...");
			Document doc = XmlUtil.parseXml(resp);
			String confString = XmlUtil.updateXmlDoc(doc,  properties);
			
			// make PUT request adding updated configuration document as payload body
			logger.debug("Publishing new configurations...");
			publisher = new Publisher(mqttClient, getPayloadMetrics(), requestTopic.toString(), confString, latch);
			publisher.start();
		}		
	}
	
	@Override
	public void getResponse(CountDownLatch latch) { 
		super.getResponse(latch);
	}
}
