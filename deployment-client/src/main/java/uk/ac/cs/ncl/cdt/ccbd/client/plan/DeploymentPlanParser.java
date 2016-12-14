package uk.ac.cs.ncl.cdt.ccbd.client.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uk.ac.cs.ncl.cdt.ccbd.unit.CloudDeployableUnit;
import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;
import uk.ac.cs.ncl.cdt.ccbd.unit.GatewayDeployableUnit;
import uk.ac.cs.ncl.cdt.ccbd.unit.Position;

/**
 * DeploymentPlanParser - Parses deployment plan and creates deployment objects.
 * @author Saleh Mohamed <s.mohamed@ncl.ac.uk>
 * @version 25th May 2016
 */
public class DeploymentPlanParser implements DeploymentPlanListener{
	
	/**
	 * Create and instance of DeploymentPlanParser.
	 * @param optimizerPort - Port number that an optimiser client socket connects to this server.
	 * @param deploymentHandlerPort - Port number for the deployment handler server in which deployment client connects to.
	 * @param serverAddress - IP/dns name of the deployment handler server
	 */
	public DeploymentPlanParser(int optimizerPort, int deploymentServerPort, String serverAddress) {
		DeploymentClientInput server = new DeploymentClientInput(optimizerPort, deploymentServerPort, serverAddress);
		server.addListener(this);
		Thread th = new Thread(server);
		th.start();
	}
	
	/**
	 * Parse JSON formatted execution plan into deployment units. Each unit can be deployed on a
	 * separate device 
	 * @param deploymentPlan - Deployment plan in json format.
	 * @return depoloyableUnits - A list of deployable units.
	 * @throws IOException
	 * @throws ParseException
	 * @throws CloneNotSupportedException 
	 */
	public List<DeployableUnit> onNewPlanReceived(String deploymentPlan) throws IOException, ParseException, CloneNotSupportedException {
		List<DeployableUnit> deployableUnits = new ArrayList<DeployableUnit>();
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(deploymentPlan);
		JSONArray streamOperators = (JSONArray)json.get("StreamOperators");
		for (int i = 0; i < streamOperators.size(); i++) {
			List<String> arguments = new ArrayList<String>();
			JSONObject op = (JSONObject)streamOperators.get(i);
			String deviceType = (String)op.get("Device");
			String opId = (String)op.get("OP_ID");
			String dataOut = (String)op.get("data_out");
			JSONArray arr = (JSONArray)op.get("Operation");
			String operationType = (String)((JSONObject)arr.get(0)).get("Type");
			String operatorLocation = (String)((JSONObject)arr.get(1)).get("Operator");
			String packageName = (String)((JSONObject)arr.get(2)).get("package_name");
			JSONArray argsArray = (JSONArray)((JSONObject)arr.get(3)).get("Arguments");
			
			if (argsArray != null) {
				for (int j = 0; j < argsArray.size(); j++) {
					arguments.add(argsArray.get(j).toString().replaceAll("[\"{}]", ""));
				}	
			}
			
			// create appropriate deployment object
			DeployableUnit du = null;
			if (deviceType.toLowerCase().equals("gateway")) {
				// create DeployableUnit for gateway device
				du = new GatewayDeployableUnit();
				operationType = addGatewaySpecificProperties((GatewayDeployableUnit)du, operationType);
				((GatewayDeployableUnit) du).setMqttBrokerIp(dataOut);
				((GatewayDeployableUnit) du).setPackageName(packageName);
				
			}else if (deviceType.toLowerCase().equals("container")){
				// create DeployableUnit for Cloud resources
				du = new CloudDeployableUnit();
				((CloudDeployableUnit) du).setMqttBrokerIp(dataOut);
				
			}else {
				du = new DeployableUnit();
				// Other deployment options will come here
			}
					
			// Set common properties
			du.setArguments(arguments);
			du.setDataOut(dataOut);
			du.setOperationType(operationType);
			du.setDeviceType(deviceType);
			du.setOperatorId(opId);
			du.setOperatorLocation(operatorLocation);
			
			if (du.getDeviceType().equals("Broker")) {
				du.setOrder(1);
			}
			if (du.getDeviceType().equals("Container")) {
				du.setOrder(2);
			}
			if (du.getDeviceType().equals("Gateway")) {
				du.setOrder(3);
			}
			
			// Set properties specific to gateway devices
			
			
			deployableUnits.add(du);
		}
		
		List<DeployableUnit> clones = new ArrayList<DeployableUnit>();
		JSONArray operatorPlacement = (JSONArray)json.get("OperatorPlacement");
		for (int i = 0; i < operatorPlacement.size(); i++) {
			JSONObject op = (JSONObject)operatorPlacement.get(i);
			String deviceId = (String)op.get("PE_ID");
			String operatorId = (String)op.get("OP_ID");
			
			// Query the catalogue for ip and position of the device
			//
			Position ps = new Position(); // add lat and long values from the catalogue
			for (DeployableUnit du : deployableUnits) {
				if (operatorId.equals(du.getOperatorId())) {
					List<String> tokens = Arrays.asList(deviceId.split(","));
					// managing or deploying to more than one gateway device, each device needs to be 
					// presented by it own DeployableUnit.
					if (tokens.size() > 1 && du.getDeviceType().equals("Gateway")) {
						for (int j = 0; j < tokens.size() - 1; j++) {
							DeployableUnit clone = (DeployableUnit)du.clone();
							clone.setDeviceId(tokens.get(j));
							//query resource catalogue for the ip and position and set them here
							clones.add(clone);
						}
						du.setDeviceId(tokens.get(tokens.size() - 1));
					}else {
						du.setDeviceId(deviceId);
					}
					
					//set ip and position here
				}
			}	
		}
		deployableUnits.addAll(clones);
		Collections.sort(deployableUnits);
		for (DeployableUnit du : deployableUnits)
			System.out.println(du.getDeviceId());
		return deployableUnits;
	}

	/**
	 * Add properties specific to field gateway deployment
	 * @param du - Deployable unit ot add additional properties.
	 * @param operationType - Type of operation to be performed in the field gateway.
	 * @return operationType
	 */
	private String addGatewaySpecificProperties(GatewayDeployableUnit du, String operationType) {
		if (operationType.startsWith("get/configurations")) {
			du.setAction("conf-v1");
			du.setMethod("get");
			
			if (operationType.split("/").length == 3)
				operationType = "configurations/" + operationType.split("/")[2];
			else
				operationType = "configurations";
			return operationType;
		}

		if (operationType.startsWith("put/configurations")) {
			du.setAction("conf-v1");
			du.setMethod("put");
			if (operationType.split("/").length == 3)
				operationType = "configurations/" + operationType.split("/")[2];
			else
				operationType = "configurations";
			return operationType;
		}

		if ("get/packages".equals(operationType) || "get/bundles".equals(operationType)) {
			du.setAction("deploy-v1");
			du.setMethod("get");
			operationType = operationType.split("/")[1];
			return operationType;
		}

		if (operationType.startsWith("stop") || operationType.startsWith("start") || operationType.startsWith("install")
				|| operationType.startsWith("uninstall")) {
			du.setAction("deploy-v1");
			du.setMethod("exec");
			return operationType;
		}
		return operationType;
	}
}
