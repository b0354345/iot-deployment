package uk.ac.cs.ncl.cdt.ccbd2014.gateway.main;

import uk.ac.cs.ncl.ccbd.cdt2014.gateway.deploymentListener.CmdArgsParser;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.MissingArgumentException;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.UnsupportedResourceException;

public class Main {
	public static void main(String[] args) throws MissingArgumentException, UnsupportedResourceException {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		if (args.length != 0) {
			// use user supplied command line args
		}
		else {
			new CmdArgsParser();
		}		
	}
}
