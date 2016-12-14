package uk.ac.cs.ncl.cdt.ccbd.cloud.listerner;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.jcraft.jsch.JSchException;

import uk.ac.cs.ncl.cdt.ccbd.cloud.util.UnsupportedResourceException;


public interface CloudDeploymentListener {
	void onNewCloudDeploymentReceived(Map<String, String> map) throws UnsupportedResourceException, InterruptedException, JSchException, IOException, ExecutionException;
}
