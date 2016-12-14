package uk.ac.cs.ncl.cdt.ccbd.client.sumilator;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptimiserSim implements Runnable{
	private final Logger logger = LoggerFactory.getLogger(OptimiserSim.class);
	private Socket socket;
	private int port;
	private String serverAddress;
	
	public OptimiserSim (String address, String listeningPort) {
		this.serverAddress = address;
		this .port = Integer.parseInt(listeningPort);
		try {
			logger.info("OPTIMIZER:  Try connection to deployment client ......");
			socket = new Socket(serverAddress, port);
			logger.info("OPTIMIZER: Connection established");
		} catch (UnknownHostException e) {
			logger.info("ERROR: Could not connect to deployement client" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(bos);
			
			logger.info("OPTIMIZER: Reading execution plan");
			FileReader fr = new FileReader(new File("install_multiple.json"));
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				osw.write(line + "\n");
				osw.flush();
			}
			logger.info("OPTIMIZER: Finished sending execution plan");
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		OptimiserSim optimizer = new OptimiserSim("localhost", "5000");
		Thread optThread = new Thread(optimizer);
		optThread.start();
	}
}
