package uk.ac.cs.ncl.ccbd.cdt2014.gateway.util;

public class MissingArgumentException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public MissingArgumentException() {}
	
	public MissingArgumentException(String message) {
		super(message);
	}
	
	public MissingArgumentException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MissingArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTree) {
		super(message, cause, enableSuppression, writableStackTree);
	}	
}
