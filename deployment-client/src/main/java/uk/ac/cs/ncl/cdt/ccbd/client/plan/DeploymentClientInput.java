package uk.ac.cs.ncl.cdt.ccbd.client.plan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

/**
 * DeploymentClientInput handles connection to the optimiser for receiving deployment plan. 
 * @author Saleh Mohamed <s.mohamed@ncl.ac.uk>
 * @version 25th May 2016
 */

public class DeploymentClientInput implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(DeploymentClientInput.class);
	private List<DeploymentPlanListener> listeners;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private InputStreamReader is;
	private OutputStreamWriter osw;
	private int deploymentHandlerPort;
	private String serverAddress;

	public DeploymentClientInput(){}

	/**
	 * Create and instance of DeploymentClientInput.
	 * @param optimizerPort - Port number that an optimiser client socket connects to this server.
	 * @param deploymentHandlerPort - Port number for the deployment handler server in which deployment client connects to.
	 * @param serverAddress - IP/dns name of the deployment handler server
	 */
	public DeploymentClientInput(int optimizerPort, int deploymentHandlerPort, String serverAddress) {
		listeners = new ArrayList<DeploymentPlanListener>();
		this.deploymentHandlerPort = deploymentHandlerPort;
		this.serverAddress = serverAddress;
		try {
			serverSocket = new ServerSocket(optimizerPort);
			logger.info("DEPLOYMENT CLIENT: Activating listener ..........");

		} catch (IOException ex) {
			logger.info("DEPLOYMENT CLIENT: Error when activating server socket listener: " + ex.getMessage());
		}
	}

	public void addListener(DeploymentPlanListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		while (true) {
			try {
				logger.info("DEPLOYMENT CLIENT: listener is active");
				clientSocket = serverSocket.accept();
				osw = new OutputStreamWriter(clientSocket.getOutputStream());
				is = new InputStreamReader(clientSocket.getInputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			StringBuffer execPlanString = new StringBuffer("");
			String line = "";
			BufferedReader rd = new BufferedReader(is);
			try {
				logger.info("DEPLOYMENT CLIENT: Reading deployment plan from optimiser .............");
				while ((line = rd.readLine()) != null) {
					execPlanString.append(line + "\n");
				}
			} catch (IOException ex) {
				logger.info("DEPLOYMENT CLIENT: Error while trying to read deployment plan from optimizer: " + ex.getMessage());

			} finally {

				try {
					is.close();
					rd.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			for (DeploymentPlanListener listener : listeners) {
				try {
					long startTime = System.currentTimeMillis();
					// Generate deployment objects from the deployment plan
					List<DeployableUnit> du = listener.onNewPlanReceived(execPlanString.toString());
					
					// test for the existence of cloud info
					for (DeployableUnit d : du) {
						System.out.println(d);
					}
					// Send deployment objects to deployment server
					DeploymentClientOutput out = new DeploymentClientOutput(du, serverAddress, deploymentHandlerPort);
					logger.info("DEPLOYMENT CLIENT: Sending deployment object to the server");
					out.sendToServer();
					
					// Receive response message from the server. The response message can be forwarded to the optimiser. 
					String response = out.receiveFromServer();
					osw.write(response);
					
					long endTime = System.currentTimeMillis();
					long deploymentTime = endTime - startTime;
					System.out.println("Start Time: " + startTime + "\nEnd Time: " +  endTime + "\nDeploymentTime: " + deploymentTime);
					logger.info("DEPLOYMENT CLIENT: Finished receiving response message");
					Path path = Paths.get("/Users/saleh/Documents/gateway.txt");
					try (BufferedWriter writer = Files.newBufferedWriter(path)) {
						writer.write("start time: " + startTime);
						writer.write("end time: " + endTime);
					    writer.write("deployment time: " + deploymentTime);
					}
				} catch (IOException | ParseException | ClassNotFoundException | CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
