package uk.ac.cs.ncl.cdt.ccbd.client.main;

import java.io.IOException;

import uk.ac.cs.ncl.cdt.ccbd.client.plan.DeploymentPlanParser;
import uk.ac.cs.ncl.cdt.ccbd.client.sumilator.DeploymentServerSim;

public class Main {
	public static void main(String[] args) throws IOException{
		new DeploymentPlanParser(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);		
	}
}
