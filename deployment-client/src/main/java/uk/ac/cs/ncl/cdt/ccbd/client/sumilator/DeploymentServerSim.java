package uk.ac.cs.ncl.cdt.ccbd.client.sumilator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cs.ncl.cdt.ccbd.client.plan.DeploymentPlanParser;
import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

public class DeploymentServerSim implements Runnable{
	private int port;
	public DeploymentServerSim() {}
	public DeploymentServerSim(int port) {
		this.port = port;
	}
	
	public void run() {
		
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Socket clientSocket = null;
			ObjectInputStream ois = null;
			ObjectOutputStream oos = null;
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
						ois = new ObjectInputStream(clientSocket.getInputStream());
						DeployableUnit du = (DeployableUnit)ois.readObject();
						System.out.println("!!!!!!!! Received by server, Deployable unit: " + du.getOrder());
						deployableUnits.add(du);
						System.out.println(du);
						int available = clientSocket.getInputStream().available();
						if (available == 0) {
							break;
						}
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
				
				// deployment.deploy(deployableUnits);   // Not implemented yet
			}
	}
	
	public static void main(String[] args) throws IOException{
		// start deployment server simulator providing client-server listening port as args[0]
		DeploymentServerSim serverSim = new DeploymentServerSim(50000);
		Thread th = new Thread(serverSim);
		th.start();
		
	}
}
