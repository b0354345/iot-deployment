package uk.ac.cs.ncl.ccbd.cdt2014.gateway.util;

import java.util.List;

public class ResourceChecker {	
	public static void checkResource(String resource, List<String> list) throws UnsupportedResourceException {
		String [] tokens = resource.split("/");
		if (!list.contains(tokens[0])) {
			throw new UnsupportedResourceException("\"" + tokens[0] + "\" is not supported by GET method");
		}
	}
}
