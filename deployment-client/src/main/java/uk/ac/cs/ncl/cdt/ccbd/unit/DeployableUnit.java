package uk.ac.cs.ncl.cdt.ccbd.unit;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DeployableUnit implements Serializable, Cloneable, Comparable<DeployableUnit>{
	private static final long serialVersionUID = -2603180586813757453L;
	private String deviceType;
	private int order;
	private String deviceId;
	private String deviceIp;
	private String operatorId;
	private String operationType;
	private String operatorLocation;
	private List<String> arguments;
	private String dataOut;
	
	public DeployableUnit () {
		
	}

	public DeployableUnit(String deviceType, String deviceId, String deviceIp, String operatorId, String operationType,
			String operatorLocation, List<String> arguments, String dataOut) {
		this.deviceType = deviceType;
		this.deviceId = deviceId;
		this.deviceIp = deviceIp;
		this.operatorId = operatorId;
		this.operationType = operationType;
		this.operatorLocation = operatorLocation;
		this.arguments = arguments;
		this.dataOut = dataOut;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String device) {
		this.deviceType = device;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOperatorLocation() {
		return operatorLocation;
	}

	public void setOperatorLocation(String operatorLocation) {
		this.operatorLocation = operatorLocation;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public String getDataOut() {
		return dataOut;
	}

	public void setDataOut(String dataOut) {
		this.dataOut = dataOut;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "DeployableUnit [device=" + deviceType + ", order=" + order + ", deviceId=" + deviceId + ", deviceIp="
				+ deviceIp + ", operatorId=" + operatorId + ", operationType="+ operationType + ", operatorLocation=" 
				+ operatorLocation + ", arguments=" + arguments + ", dataOut=" + dataOut + "]";
	}

	@Override
	public int compareTo(DeployableUnit object) {
		return this.order - ((DeployableUnit)object).getOrder();
	}	
	
	@Override
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
	} 
}
