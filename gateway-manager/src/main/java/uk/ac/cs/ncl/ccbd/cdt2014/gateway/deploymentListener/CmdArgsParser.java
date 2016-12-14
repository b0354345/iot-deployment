package uk.ac.cs.ncl.ccbd.cdt2014.gateway.deploymentListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity.BundleOperationRequestHandler;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity.ConfigurationRequestHandler;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity.PackageOperationRequestHandler;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.activity.RequestHandlerImpl;
import uk.ac.cs.ncl.ccbd.cdt2014.gateway.util.UnsupportedResourceException;

public class CmdArgsParser implements CmdArgsListener{
	private final Logger logger = LoggerFactory.getLogger(CmdArgsParser.class);
	private ExecutorService execService;
	private int count;

	public CmdArgsParser() {
		execService = Executors.newFixedThreadPool(35);
		count = 0;
		GatewayServerSocket gss = new GatewayServerSocket();
		gss.addListener(this);
		Thread th = new Thread(gss);
		th.start();
	}

	@Override
	public void onNewGatewayDeploymentReceived(String[] args) throws UnsupportedResourceException, InterruptedException, ExecutionException {
		execService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					parseArgs(args);
					++count;
				} catch (UnsupportedResourceException e) {
					e.printStackTrace();
				}	
			}
		});		
	}

	
	public void parseArgs(String[] args) throws UnsupportedResourceException {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("deployer");
		parser.addArgument("-d", "--device").required(true)
			.help("ID of the target gateway device");
		parser.addArgument("-b", "--broker").required(true)
			.help("MQTT server for client to publish and subscribe to");
		parser.addArgument("-a", "--action").required(true)
			.help("Client action to the remote device. Currently only deployment and configuration are supported")
			.choices("conf-v1", "CONF-V1", "deploy-v1", "DEPLOY-V1");
		parser.addArgument("-m", "--method").required(true)
			.help("http CRUD-like methods for REST services")
			.choices("get", "GET", "put", "PUT", "exec", "EXEC");
		parser.addArgument("-r", "--resource").required(true)
			.help("Type of operation");
		parser.addArgument("-p", "--properties").nargs("+").help("Configurable properties to be updated. Properties are passed as "
				+ "key-value pairs separated by ':'");
		//parser.addArgument("-v", "--value").help("New value for the configurable property to be updated");
		parser.addArgument("-u", "--deploy.url").help("Url identifying the location of the package to be installed in a remote device");
		parser.addArgument("-pkg", "--deploy.pkg.name").help("Name of the package to be uninstalled from a remote device");
		
		Namespace res = null;
		
		logger.info("GATEWAY MANAGER: Received command line arguments:");
		for (String arg : args) {
			logger.info(arg);
		}
		try {
			res = parser.parseArgs(args);
		} catch (ArgumentParserException e1) {
			e1.getMessage();
		}

		if (res == null)
			return;
		
		String deviceId = res.getString("device");
		String broker = res.getString("broker");
		String action = res.getString("action").toUpperCase();
		String method = res.getString("method").toUpperCase();
		String resource = res.getString("resource");
		List<String> properties = res.getList("properties");
		//String value = res.getString("value");
		String deployUrl = res.getString("deploy.url");
		String deployPackageName = res.getString("deploy.pkg.name");

		CountDownLatch latch = new CountDownLatch(2);

		String [] tokens = resource.split("/");
		List<String> bundleOps = Arrays.asList("bundles", "start", "stop");
		List<String> packageOps = Arrays.asList("packages", "install", "uninstall");
		
		// Handling configuration related request
		if ("configurations".equals(tokens[0])) {	
			handleConfigurationRequest(deviceId, broker, action, method, resource, properties, latch);
		
		// Handling bundle related operations request	
		}else if (bundleOps.contains(tokens[0])) {
			handleBundleOpRequest(deviceId, broker, action, method, resource, latch);
		
		// Handling package related operations request
		}else if (packageOps.contains(tokens[0])) {		
			handlePackageOpRequest(deviceId, broker, action, method, resource, deployUrl, deployPackageName, latch);
			
		} else {
			throw new UnsupportedResourceException("\"" + resource.split("/")[0] + "\" not supported");
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void handlePackageOpRequest(String deviceId, String broker, String action, String method, String resource,
			String deployUrl, String deployPackageName, CountDownLatch latch) throws UnsupportedResourceException {
		if (!"DEPLOY-V1".equals(action))
			throw new UnsupportedResourceException("Expecting \"DEPLOY-V1\" instead of \"" +  action + "\"");
		if (!"GET".equals(method) && !"EXEC".equals(method))
			throw new UnsupportedResourceException("Expecting \"GET\" or \"EXEC\" instead of \"" +  method + "\"");
		
		RequestHandlerImpl req = new PackageOperationRequestHandler(deviceId, broker, action, method, resource, deployUrl, deployPackageName);
		try {
			req.makeRequest(latch);
			req.getResponse(latch);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleBundleOpRequest(String deviceId, String broker, String action, String method, String resource,
			CountDownLatch latch) throws UnsupportedResourceException {
		if (!"DEPLOY-V1".equals(action))
			throw new UnsupportedResourceException("Expecting \"DEPLOY-V1\" instead of \"" +  action + "\"");
		if (!"GET".equals(method) && !"EXEC".equals(method))
			throw new UnsupportedResourceException("Expecting \"GET\" or \"EXEC\" instead of \"" +  method + "\"");
		
		RequestHandlerImpl req = new BundleOperationRequestHandler(deviceId, broker, action, method, resource);
		try {
			req.makeRequest(latch);
			req.getResponse(latch);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleConfigurationRequest(String deviceId, String broker, String action, String method, String resource,
			List<String> properties, CountDownLatch latch) throws UnsupportedResourceException {
		if (!"CONF-V1".equals(action))
			throw new UnsupportedResourceException("Expecting \"CONF-V1\" instead of \"" +  action + "\"");
		if (!"GET".equals(method) && !"PUT".equals(method))
			throw new UnsupportedResourceException("Expecting \"GET\" or \"PUT\" instead of \"" +  method + "\"");
		
		RequestHandlerImpl confRequest = new ConfigurationRequestHandler(deviceId, broker, action, method, resource, properties);
		try {
			confRequest.makeRequest(latch);
			confRequest.getResponse(latch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	}


