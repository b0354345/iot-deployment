package uk.ac.cs.ncl.cdt.ccbd.cloud.listerner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import uk.ac.cs.ncl.cdt.ccbd.cloud.cluster.ManagerNode;
import uk.ac.cs.ncl.cdt.ccbd.cloud.cluster.WorkerNode;
import uk.ac.cs.ncl.cdt.ccbd.cloud.util.UnsupportedResourceException;

public class CloudScriptsLauncher implements CloudDeploymentListener {
	private final Logger logger = LoggerFactory.getLogger(CloudScriptsLauncher.class);
	private long startTime;
	private long endTime;
	private long deploymentTime;
	private int workerCount;
	public CloudScriptsLauncher() {
		CloudServerSocket css = new CloudServerSocket();
		css.addListener(this);
		Thread th = new Thread(css);
		th.start();
	}

	@Override
	public void onNewCloudDeploymentReceived(Map<String, String> map) throws UnsupportedResourceException, InterruptedException, JSchException, IOException, ExecutionException {
		startTime = System.currentTimeMillis();
		logger.info("CLOUD MANAGER: Starting cloud deployment");
		String operationType = map.get("operation-type");
		String nodes = map.get("nodes");
		List<String> nodeList = Arrays.asList(nodes.split(","));
		List<String> workerList = new ArrayList<String>(nodeList.subList(1,  nodeList.size()));
		String managerAddress = nodeList.get(0);
		String resourceLocation = map.get("url");
		String mainClass = map.get("main-class");
		String broker = map.get("broker-address") + ":" + map.get("broker-port");
		String topic = map.get("topic");
		int batchSize = Integer.parseInt(map.get("batch-size"));
		if (operationType.toLowerCase().equals("deploy")) {
			// Manager node initialises a swarm cluster
			logger.info("CLOUD MANAGER: Launching Swarm manager");
			ManagerNode mng = new ManagerNode(managerAddress, resourceLocation, mainClass, broker, topic, batchSize);
			//CountDownLatch managerLatch = new CountDownLatch(1);
			String initOut = mng.initSwarmCluster(nodeList);
			System.out.println("Response from manager node: " + initOut);
			//managerLatch.await();
			logger.info("CLOUD MANAGER: Swarm manager launched successfully");
			System.out.println("test test tes ............................................1!!!!!!!!!!!!!!");
			logger.info("CLOUD MANAGER: Launching Swarm workers");
			// Worker nodes join the swarm cluster
			CountDownLatch workerLatch = new CountDownLatch(nodeList.size() - 1);
			
			// create a thread pool of worker nodes that join the swarm cluster
			ExecutorService executor1 = Executors.newFixedThreadPool(workerList.size());
			Set<Callable<String>> callables = new HashSet<Callable<String>>();
			for (String worker : workerList) {
				System.out.println("Connecting to " + worker);
				Callable<String> workerNode = new WorkerNode(worker, workerLatch);
				callables.add(workerNode);
			}
			List<Future<String>> futures = executor1.invokeAll(callables);
			
			for(Future<String> future : futures){
			    System.out.println("Response from worker " + ++workerCount + ": = " + future.get());
			}
			workerLatch.await();
			executor1.shutdown();
			logger.info("CLOUD MANAGER: Swarm workers launched successfully");
			
			logger.info("CLOUD MANAGER: Launching worker service for Spark workers");
			// Launch the Spark "start-slave.sh" script from each the manager node as docker worker service
			CountDownLatch managerLatch2 = new CountDownLatch(1);
			String workerServiceOutput = mng.startSparkSlaveService(managerLatch2);
			System.out.println("Response from worker services: " + workerServiceOutput);
			managerLatch2.await();
			logger.info("CLOUD MANAGER: Spark workers launched successfully");
			
			logger.info("CLOUD MANAGER: Launching master service for Spark master");
			// Launch the Spark "start-master.sh" script from the manager node as a docker manager service
			String managerServiceOutput = mng.startSparkMasterService();
			System.out.println("Response from manager service: " + managerServiceOutput);
			logger.info("CLOUD MANAGER: Spark manager launched successfully");
			endTime = System.currentTimeMillis();
			deploymentTime = endTime - startTime;
			System.out.println("Start time: " + startTime + "\nEnd time: " + endTime + "\nCloud deployment time: " + deploymentTime);
		}	
	}
}
