package uk.ac.cs.ncl.cdt.ccbd.handler.server;

import java.io.IOException;
import java.util.List;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

public interface DeploymentObjectsListener {
	void onNewDeploymentObjectsReceived(List<DeployableUnit> list) throws IOException;
}
