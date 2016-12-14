package uk.ac.cs.ncl.cdt.ccbd.cloud.cluster;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import uk.ac.cs.ncl.cdt.ccbd.cloud.util.SshChannel;

public class WorkerNode implements Callable<String>{
	private final Logger logger = LoggerFactory.getLogger(WorkerNode.class);
	private String nodeAddress;
	private CountDownLatch latch;
	private final String USER = "add your vm user";
	private final String PWD = "add ssh password for your vms";
	private final String INIT_SCRIPT_SOURCE = "swarm-join.sh";
	private final String INIT_SCRIPT_DEST = "swarm-join.sh";
	
	public WorkerNode(String nodeAddress, CountDownLatch latch) {
		this.nodeAddress = nodeAddress;
		this.latch = latch;
	}
	
	public String joinSwarmCluster() throws JSchException, IOException {
		StringBuilder sb = new StringBuilder("");
		sb.append("/bin/bash swarm-join.sh");
		String command = sb.toString();
		SshChannel ssh = new SshChannel();
		ssh.sendFile(USER, PWD, nodeAddress, INIT_SCRIPT_SOURCE, INIT_SCRIPT_DEST);
		String output = ssh.execCommand(USER, PWD, nodeAddress, command);
		latch.countDown();
		return output;
	}

	@Override
	public String call() {
		String output = "";
		try {
			output = joinSwarmCluster();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
}
