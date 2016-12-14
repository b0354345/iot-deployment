package uk.ac.cs.ncl.cdt.ccbd.handler.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.CloudDeployableUnit;
import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

public class CloudHandler {
		private final Logger logger = LoggerFactory.getLogger(CloudHandler.class);
		private final String ADDRESS = "localhost";
		private final int PORT = 40001;
		private Map<String, String> map;
		private CloudDeployableUnit deployableUnit;
		private ObjectOutputStream oos = null;
		private InputStreamReader is = null;
		private Socket socket;

		public CloudHandler(DeployableUnit deployableUnit) {
			map = new HashMap<String, String>();
			this.deployableUnit = (CloudDeployableUnit) deployableUnit;

			try {
				logger.info("DEPLOYMENT HANDLER:  Try connection to cloud manager ......");
				socket = new Socket(ADDRESS, PORT);
				logger.info("DEPLOYMENT HANDLER: Connection with cloud manager established");
			} catch (Exception e) {
				logger.info("DEPLOYMENT HANDLER: Could not connect to cloud manager: " + e.getMessage());
			}
		}
		
		public Map<String, String> prepareCmdArguments() {
			Map<String, String> args = new HashMap<String,String>();
			String url = deployableUnit.getOperatorLocation();
			String nodes = deployableUnit.getDeviceId();
			String operationType = deployableUnit.getOperationType();
			//String brokerAddress = deployableUnit.getMqttBrokerIp();
			for (String str : deployableUnit.getArguments()) {
				String key = str.split(":")[0];
				String value = str.split(":")[1];
				args.put(key, value);
			}
			map.put("url", url);
			map.put("nodes", nodes);
			map.put("operation-type", operationType);
			map.put("main-class", args.get("main.class"));
			map.put("broker-address",  args.get("broker.address"));
			map.put("broker-port", args.get("broker.port"));
			map.put("topic", args.get("subscribe.topic"));
			map.put("batch-size", args.get("rdd.bachsize"));
			return map;
		}
		
		public void sendToCloudManager(Map<String, String> map) throws IOException {
			logger.info("DEPLOYMENT HANDLER: Sending object to cloud manager");
			oos = new ObjectOutputStream(socket.getOutputStream());
			is = new InputStreamReader(socket.getInputStream());
			oos.writeObject(map);
			oos.flush();
			logger.info("DEPLOYMENT HANDLER: Object successfully sent to the cloud manager");
		}
}
