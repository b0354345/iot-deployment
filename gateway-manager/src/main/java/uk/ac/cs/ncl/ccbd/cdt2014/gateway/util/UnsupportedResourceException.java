package uk.ac.cs.ncl.ccbd.cdt2014.gateway.util;

public class UnsupportedResourceException extends Exception{
private static final long serialVersionUID = 1L;
	
	public UnsupportedResourceException() {}
	
	public UnsupportedResourceException(String message) {
		super(message);
	}
	
	public UnsupportedResourceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UnsupportedResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTree) {
		super(message, cause, enableSuppression, writableStackTree);
	}	
}
