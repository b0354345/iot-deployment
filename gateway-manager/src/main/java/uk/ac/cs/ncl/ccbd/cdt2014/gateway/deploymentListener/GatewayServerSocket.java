package uk.ac.cs.ncl.ccbd.cdt2014.gateway.deploymentListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.UnsupportedResourceException;

public class GatewayServerSocket implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(GatewayServerSocket.class);
	private List<CmdArgsListener> listeners;
	private final int PORT = 50001;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectInputStream ois;
	private static OutputStreamWriter osw;

	public GatewayServerSocket() {
		listeners = new ArrayList<CmdArgsListener>();
		try {
			serverSocket = new ServerSocket(PORT);
			logger.info("GATEWAY MANAGER: Activating server socket listener ..........");

		} catch (IOException ex) {
			logger.info("GATEWAY MANAGER: Error when activating server socket listener: " + ex.getMessage());
		}
	}
	
	public void addListener(CmdArgsListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		while (true) {
			String [] args = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				try {
					osw = new OutputStreamWriter(clientSocket.getOutputStream());
					ois = new ObjectInputStream(clientSocket.getInputStream());
					args = (String [])ois.readObject();
					
					int available = clientSocket.getInputStream().available();
					if (available == 0) {
						break;
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}			
			}
			for (CmdArgsListener listener : listeners) {
				try {
					listener.onNewGatewayDeploymentReceived(args);
				} catch (UnsupportedResourceException | InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}	
	}

	public static OutputStreamWriter getOsw() {
		return osw;
	}
}
