package uk.ac.cs.ncl.cdt.ccbd.cloud.listerner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import uk.ac.cs.ncl.cdt.ccbd.cloud.util.UnsupportedResourceException;

public class CloudServerSocket implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(CloudServerSocket.class);
	private List<CloudDeploymentListener> listeners;
	private final int PORT = 40001;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectInputStream ois;
	private static OutputStreamWriter osw;

	public CloudServerSocket() {
		listeners = new ArrayList<CloudDeploymentListener>();
		try {
			serverSocket = new ServerSocket(PORT);
			logger.info("CLOUD MANAGER: Activating server socket listener ..........");

		} catch (IOException ex) {
			logger.info("CLOUD MANAGER: Error when activating server socket listener: " + ex.getMessage());
		}
	}

	public void addListener(CloudDeploymentListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		while (true) {
			Map<String, String> map = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					osw = new OutputStreamWriter(clientSocket.getOutputStream());
					ois = new ObjectInputStream(clientSocket.getInputStream());
					map = (Map<String, String>) ois.readObject();

					int available = clientSocket.getInputStream().available();
					if (available == 0) {
						break;
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			for (CloudDeploymentListener listener : listeners) {
				try {
					map.forEach((k,v)-> System.out.println(k+", "+v));
					try {
						listener.onNewCloudDeploymentReceived(map);
					} catch (JSchException | IOException | ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnsupportedResourceException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static OutputStreamWriter getOsw() {
		return osw;
	}

}
