package uk.ac.cs.ncl.cdt.ccbd.unit;

import java.util.List;

public class GatewayDeployableUnit extends DeployableUnit {
	private static final long serialVersionUID = -3931392965304584042L;
	
	private Position position;
	private String method;
	private String action;
	private String mqttBrokerIp;
	private String packageName;

	public GatewayDeployableUnit() {
	}

	public GatewayDeployableUnit(String deviceType, String deviceId, String deviceIp, String operatorId, String operationType, 
			String operatorLocation, List<String> arguments, String dataOut, Position position) {
		super(deviceType, deviceId, deviceIp, operatorId, operationType, operatorLocation, arguments, dataOut);	
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMqttBrokerIp() {
		return mqttBrokerIp;
	}

	public void setMqttBrokerIp(String mqttBrokerIp) {
		this.mqttBrokerIp = mqttBrokerIp;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "GatewayDeployableUnit [device=" + super.getDeviceType() + ", deviceId=" + super.getDeviceId() + ", deviceIp="
				+ super.getDeviceIp() + ", operatorId=" + super.getOperatorId() + ", operationType="+ super.getOperationType() + ", operatorLocation=" 
				+ super.getOperatorLocation() + ", arguments=" + super.getArguments() + ", dataOut=" + super.getDataOut() + ", position=" + position + ", method=" + method + ", action=" + action
				+ ", mqttBrokerIp=" + mqttBrokerIp +  ", packageName=" + packageName + ", order=" + super.getOrder() + ", arguments=" + super.getArguments() + "]";
	}
}
