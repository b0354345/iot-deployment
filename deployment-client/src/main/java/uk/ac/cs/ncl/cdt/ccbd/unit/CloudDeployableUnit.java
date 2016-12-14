package uk.ac.cs.ncl.cdt.ccbd.unit;

import java.util.List;

public class CloudDeployableUnit extends DeployableUnit {
	private static final long serialVersionUID = -691571016105363852L;
	private String mainClass;
	private String mqttBrokerIp;

	public CloudDeployableUnit() {
	}

	public CloudDeployableUnit(String device, String deviceId, String deviceIp, String operatorId, String operationType,
			String operatorLocation, List<String> arguments, String dataOut) {
		super(device, deviceId, deviceIp, operatorId, operationType, operatorLocation, arguments, dataOut);
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public String getMqttBrokerIp() {
		return mqttBrokerIp;
	}

	public void setMqttBrokerIp(String mqttBrokerIp) {
		this.mqttBrokerIp = mqttBrokerIp;
	}
	
	@Override
	public String toString() {
		return "GatewayDeployableUnit [device=" + super.getDeviceType() + ", deviceId=" + super.getDeviceId() + ", deviceIp="
				+ super.getDeviceIp() + ", operatorId=" + super.getOperatorId() + ", operationType="+ super.getOperationType() + ", operatorLocation=" 
				+ super.getOperatorLocation() + ", arguments=" + super.getArguments() + ", dataOut=" + super.getDataOut() + ", mainClass=" + mainClass + 
				", mqttBrokerAddress=" + mqttBrokerIp + ", order=" + super.getOrder() + ", arguments=" + super.getArguments() + "]";
	}
}
