package uk.ac.cs.ncl.ccbd.cdt2014.gateway.deploymentListener;

import java.util.concurrent.ExecutionException;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.UnsupportedResourceException;

public interface CmdArgsListener {
	void onNewGatewayDeploymentReceived(String [] cdmArgs) throws UnsupportedResourceException, InterruptedException, ExecutionException;
}
