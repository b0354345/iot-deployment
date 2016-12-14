package uk.ac.cs.ncl.cdt.ccbd.handler.server;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

public class ObjectRouting implements DeploymentObjectsListener {
	private final Logger logger = LoggerFactory.getLogger(ObjectRouting.class);
	private GatewayHandler gatewayHandler;
	private CloudHandler cloudHandler;

	public ObjectRouting() {
		DeploymentObjectReceiver dor = new DeploymentObjectReceiver();
		dor.addDepObjListener(this);

		Thread th = new Thread(dor);
		th.start();
	}

	@Override
	public void onNewDeploymentObjectsReceived(List<DeployableUnit> list) throws IOException {
		for (DeployableUnit du : list) {
			if (du.getDeviceType().toLowerCase().equals("gateway")) {
				gatewayHandler = new GatewayHandler(du);
				gatewayHandler.sendToGatewayManager(gatewayHandler.createCmdLineArgs());
				gatewayHandler.receiveFromGatewayManager();
			}
			
			if (du.getDeviceType().toLowerCase().equals("container")) {
				cloudHandler = new CloudHandler(du);
				cloudHandler.sendToCloudManager(cloudHandler.prepareCmdArguments());
			}
		}
	}

}
