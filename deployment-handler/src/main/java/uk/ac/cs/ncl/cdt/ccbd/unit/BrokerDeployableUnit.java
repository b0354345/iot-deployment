package uk.ac.cs.ncl.cdt.ccbd.unit;

import java.util.List;

public class BrokerDeployableUnit extends DeployableUnit {
	private static final long serialVersionUID = -630737426041973304L;

	public BrokerDeployableUnit() {
	}

	public BrokerDeployableUnit(String device, String deviceId, String deviceIp, String operatorId, String operationType,
			String operatorLocation, List<String> arguments, String dataOut) {
		super(device, deviceId, deviceIp, operatorId, operationType, operatorLocation, arguments, dataOut);
	}
}
