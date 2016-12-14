package uk.ac.cs.ncl.cdt.ccbd.handler.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;
import uk.ac.cs.ncl.cdt.ccbd.unit.GatewayDeployableUnit;

public class GatewayHandler {
	private final Logger logger = LoggerFactory.getLogger(GatewayHandler.class);
	private final String ADDRESS = "localhost";
	private final int PORT = 50001;
	private List<String> list;
	private GatewayDeployableUnit deployableUnit;
	private ObjectOutputStream oos = null;
	private InputStreamReader is = null;
	private Socket socket;

	public GatewayHandler(DeployableUnit deployableUnit) {
		list = new ArrayList<String>();
		this.deployableUnit = (GatewayDeployableUnit) deployableUnit;

		try {
			logger.info("DEPLOYMENT HANDLER:  Try connection to gateway manager ......");
			socket = new Socket(ADDRESS, PORT);
			logger.info("DEPLOYMENT HANDLER: Connection with gateway manager established");
		} catch (Exception e) {
			logger.info("DEPLOYMENT HANDLER: Could not connect to gateway manager: " + e.getMessage());
		}
	}

	/**
	 * Prepare the command line arguments that can be accepted by the gateway
	 * manager.
	 * 
	 * @return - An array containing all the cmd line args required for
	 *         particular type of request.
	 */
	public String[] createCmdLineArgs() {
		String deviceId = deployableUnit.getDeviceId();
		String broker = deployableUnit.getMqttBrokerIp();
		String action = deployableUnit.getAction();
		String method = deployableUnit.getMethod();
		String resource = deployableUnit.getOperationType();
		List<String> properties = deployableUnit.getArguments();
		String deployUrl = deployableUnit.getOperatorLocation();
		String packageName = deployableUnit.getPackageName();
		list.add("-d");
		list.add(deviceId);
		list.add("-b");
		list.add(broker);
		list.add("-a");
		list.add(action);
		list.add("-m");
		list.add(method);
		list.add("-r");
		list.add(resource);

		if (deployUrl != null) {
			list.add("-u");
			list.add(deployUrl);
		}
		if (packageName != null) {
			list.add("-pkg");
			list.add(packageName);
		}
		if (properties != null) {
			list.add("-p");
			for (String property : properties) {
				list.add(property);
			}
		}

		String[] cmdLineArgs = new String[list.size()];
		cmdLineArgs = list.toArray(cmdLineArgs);
		return cmdLineArgs;
	}

	/**
	 * Send the cmd line args to the gateway manager through socket
	 * 
	 * @param cmdLineArgs
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendToGatewayManager(String[] cmdLineArgs) throws IOException {
		logger.info("DEPLOYMENT HANDLER: Sending object to gateway manager");
		oos = new ObjectOutputStream(socket.getOutputStream());
		is = new InputStreamReader(socket.getInputStream());
		oos.writeObject(cmdLineArgs);
		oos.flush();
		logger.info("DEPLOYMENT HANDLER: Object successfully sent to the gateway manager");
	}

	/**
	 * Receive response message of Gateway Manager and pass it as an output stream for the server socket
	 * @throws IOException
	 */
	public void receiveFromGatewayManager() throws IOException {
		// response message from gateway manager
		String line = "";
		BufferedReader rd = new BufferedReader(is);
		StringBuffer sb = new StringBuffer("");
		logger.info("DEPLOYMENT HANDLER: Receiving response from gateway manager .............");
		while (true) {
			line = rd.readLine();
			System.out.println(line);
			if (line.contains("EOF"))
				break;
			sb.append(line + "\n");
		}
		OutputStreamWriter osw = DeploymentObjectReceiver.getOsw();
		osw.write(sb.toString());
		osw.flush();
		osw.write("\nEOF");
		osw.flush();
		//osw.close(); 
		logger.info("DEPLOYMENT HANDLER: Response received and successfully set to the deployment client");
	}
}
