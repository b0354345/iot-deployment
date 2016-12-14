package uk.ac.cs.ncl.cdt.ccbd.client.plan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

/**
 * DeploymentClientOutput handles connection to the deployment server for sending deployment objects.
 * @author Saleh Mohamed <s.mohamed@ncl.ac.uk>
 * @version 25th May 2016
 */
public class DeploymentClientOutput {
	private final Logger logger = LoggerFactory.getLogger(DeploymentClientOutput.class);
	private List<DeployableUnit> deploymentUnits;
	private Socket socket;
	private ObjectOutputStream oos = null;
	private InputStreamReader isr = null;
	
	/**
	 * Create an instance of DeploymentClientOutput.
	 * @param deployableUnits - List of DeployableUnit objects
	 * @param address - Deployment server address to connect to.
	 * @param port - Deployment server listening port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public DeploymentClientOutput(List<DeployableUnit> deployableUnits, String address, int port) throws UnknownHostException, IOException {
		this.deploymentUnits = deployableUnits;
		try {
			logger.info("DEPLOYMENT CLIENT:  Try connection to deployment server ......");
			socket = new Socket(address, port);
			logger.info("DEPLOYMENT CLIENT: Connection with deployment server established");
		} catch (Exception e) {
			logger.info("DEPLOYMENT CLIENT: Could not connect to deployement server: " + e.getMessage());
		}
	}
	
	/**
	 * Send deployment objects to the deployment server through socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendToServer() throws IOException, ClassNotFoundException {
		 for (DeployableUnit du : deploymentUnits) {
			 logger.info("DEPLOYMENT CLIENT: Sending deployment oject: " + du.getDeviceId());
			 oos = new ObjectOutputStream(socket.getOutputStream());
			 oos.writeObject(du);
			 oos.flush();
			 logger.info("DEPLOYMENT CLIENT: Successfully sent deployment oject: " + du.getDeviceId());
		 }
		 logger.info("DEPLOYMENT CLIENT: Finished sending deployment objects to the deployment server");
	}
	
	/**
	 * Receive response from the deployment server.
	 * @throws IOException
	 */
	public String receiveFromServer() throws IOException {  /// return the response from the first device only
		int count = 0;
		isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		StringBuffer sb = new StringBuffer(line);
		logger.info("DEPLOYMENT CLIENT: Receiving response from deployment server");
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if (line.contains("EOF")) {
				System.out.println(++count);
				System.out.println("!!!!!!!!!!!!!!!!!1 end time: " + System.currentTimeMillis());
			}
			sb.append(line);
		}
		logger.info("DEPLOYMENT HANDLER: Finished receiving response from the server");
		return sb.toString();
	}
}
