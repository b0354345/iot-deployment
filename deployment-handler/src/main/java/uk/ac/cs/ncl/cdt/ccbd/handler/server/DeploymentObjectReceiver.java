package uk.ac.cs.ncl.cdt.ccbd.handler.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

/**
 * DeploymentObjectReceiver - Handles connection with deployment client for receiving deployment objects
 * @author Saleh Mohamed <s.mohamed@ncl.ac.uk>
 * @version 3rd June 2016
 */
public class DeploymentObjectReceiver implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(DeploymentObjectReceiver.class);
	private List<DeploymentObjectsListener> depObjectListeners;

	private int DEPLOYMENT_HANDLER_PORT = 50000;
	private ServerSocket serverSocket;
	private static Socket clientSocket;
	private ObjectInputStream ois;
	private static OutputStreamWriter osw;

	public DeploymentObjectReceiver() {
		depObjectListeners = new ArrayList<DeploymentObjectsListener>();

		try {
			serverSocket = new ServerSocket(DEPLOYMENT_HANDLER_PORT);
			logger.info("DEPLOYMENT HANDLER: Activating server socket listener ..........");

		} catch (IOException ex) {
			logger.info("DEPLOYMENT HANDLER: Error when activating server socket listener: " + ex.getMessage());
		}
	}

	public void addDepObjListener(DeploymentObjectsListener listener) {
		depObjectListeners.add(listener);
	}

	public void run() {
		List<DeployableUnit> deployableUnits = null;
		while (true) {
			deployableUnits = new ArrayList<DeployableUnit>();
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					osw = new OutputStreamWriter(clientSocket.getOutputStream());
					ois = new ObjectInputStream(clientSocket.getInputStream());
					DeployableUnit du = (DeployableUnit) ois.readObject();
					System.out.println("&&&&&&&&&&&&& " + du);
					deployableUnits.add(du);
					int available = clientSocket.getInputStream().available();
					if (available == 0) {
						break;
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			for (DeploymentObjectsListener listener : depObjectListeners) {
				try {
					listener.onNewDeploymentObjectsReceived(deployableUnits);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static OutputStreamWriter getOsw() throws IOException {
		return osw;
	}
}
