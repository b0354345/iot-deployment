package uk.ac.cs.ncl.cdt.ccbd.cloud.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SshChannel {
	
	public SshChannel(){};
	
	public String execCommand(String username, String password, String host, String command) throws JSchException, IOException {
	    JSch js = new JSch();
	    Session s = js.getSession(username, host, 22);
	    s.setPassword(password);
	    Properties config = new Properties();
	    config.put("StrictHostKeyChecking", "no");
	    s.setConfig(config);
	    s.connect();

	    Channel c = s.openChannel("exec");
	    ChannelExec ce = (ChannelExec) c;

	    ce.setCommand(command);
	    ce.setErrStream(System.err);

	    ce.connect();

	    BufferedReader reader = new BufferedReader(new InputStreamReader(ce.getInputStream()));
	    String line;
	    StringBuilder sb = new StringBuilder("");
	    while ((line = reader.readLine()) != null) {
	    	sb.append(line + "\n");
	    }

	    ce.disconnect();
	    s.disconnect();

	    System.out.println("Exit code: " + ce.getExitStatus());
	    return sb.toString();
	  }
	
	public void sendFile(String username, String password, String host, String source, String destination) {
		JSch jsch = new JSch();
	    Session session = null;
	    Channel channel = null;
	    try {
	        session = jsch.getSession(username, host, 22);
	        session.setConfig("StrictHostKeyChecking", "no");
	        session.setPassword(password);
	        session.connect();

	        channel = session.openChannel("sftp");
	        channel.connect();
	        ChannelSftp sftpChannel = (ChannelSftp) channel;

	        sftpChannel.put(source, destination);
	        sftpChannel.exit();
	        session.disconnect();
	    } catch (JSchException e) {
	        e.printStackTrace(); 
	    } catch (SftpException e) {
	        e.printStackTrace();
	    }
	    channel.disconnect();
	    session.disconnect();
	}

}
