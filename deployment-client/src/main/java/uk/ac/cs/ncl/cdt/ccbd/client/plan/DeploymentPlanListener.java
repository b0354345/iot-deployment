package uk.ac.cs.ncl.cdt.ccbd.client.plan;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.simple.parser.ParseException;

import uk.ac.cs.ncl.cdt.ccbd.unit.DeployableUnit;

public interface DeploymentPlanListener {
	List<DeployableUnit> onNewPlanReceived(String deploymentPlan) throws IOException, ParseException, CloneNotSupportedException;
}
