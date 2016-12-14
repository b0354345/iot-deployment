package uk.ac.cs.ncl.cdt.ccbd.cloud.cluster;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import uk.ac.cs.ncl.cdt.ccbd.cloud.util.SshChannel;

public class ManagerNode {
	private final Logger logger = LoggerFactory.getLogger(ManagerNode.class);
	private String nodeAddress;
	private String resourceLocation;
	private String mainClass;
	private String joinToken;
	private String broker;
	private String topic;
	private int batchSize;
	private final String USER = "add your vm user";
	private final String PWD = "add ssh password for your vms";
	private final String INIT_SCRIPT_SOURCE = "swarm-init.sh";
	private final String INIT_SCRIPT_DEST = "swarm-init.sh";
	private final String WORKER_SERVICE_SOURCE = "worker-service.sh";
	private final String WORKER_SERVICE_DEST = "worker-service.sh";
	private final String MASTER_SERVICE_SOURCE = "master-service.sh";
	private final String MASTER_SERVICE_DEST = "master-service.sh";
	
	public ManagerNode(String nodeAddress, String resourceLocation, String mainClass, String broker, String topic, int batchSize) {
		this.nodeAddress = nodeAddress;
		this.resourceLocation = resourceLocation;
		this.mainClass = mainClass;
		this.broker = broker;
		this.topic = topic;
		this.batchSize = batchSize;
	}

	public String initSwarmCluster(List<String> nodes) throws JSchException, IOException {
		StringBuilder sb = new StringBuilder("");
		sb.append("/bin/bash swarm-init.sh");
		for (String node : nodes) {
			sb.append(" " + node);
		}
		String command = sb.toString();
		SshChannel ssh = new SshChannel();
		ssh.sendFile(USER, PWD, nodeAddress, INIT_SCRIPT_SOURCE, INIT_SCRIPT_DEST);
		String output = ssh.execCommand(USER, PWD, nodeAddress, command);
		return output;
	}
	
	public String startSparkSlaveService(CountDownLatch latch) throws JSchException, IOException {
		StringBuilder sb = new StringBuilder("");
		sb.append("/bin/bash worker-service.sh");
		String command = sb.toString();
		SshChannel ssh = new SshChannel();
		ssh.sendFile(USER, PWD, nodeAddress, WORKER_SERVICE_SOURCE, WORKER_SERVICE_DEST);
		String output = ssh.execCommand(USER, PWD, nodeAddress, command);
		latch.countDown();
		return output;
	}
	
	public String startSparkMasterService() throws JSchException, IOException {
		StringBuilder sb = new StringBuilder("");
		sb.append("/bin/bash master-service.sh ");
		sb.append(resourceLocation + " ");
		sb.append(mainClass + " ");
		sb.append(broker + " ");
		sb.append(topic + " ");
		sb.append(batchSize);
		String command = sb.toString();
		System.out.println(command);
		SshChannel ssh = new SshChannel();
		ssh.sendFile(USER, PWD, nodeAddress, MASTER_SERVICE_SOURCE, MASTER_SERVICE_DEST);
		String output = ssh.execCommand(USER, PWD, nodeAddress, command);
		return output;	
	}

	public String getJoinToken() {
		return joinToken;
	}

	public void setJoinToken(String joinToken) {
		this.joinToken = joinToken;
	}
}
