package uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity;

import java.io.IOException;

public interface ResponseListener {	
	void onMqttResponseArrived(String message) throws IOException;
}
