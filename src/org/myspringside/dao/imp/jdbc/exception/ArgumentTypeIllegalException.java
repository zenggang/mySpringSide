package org.myspringside.dao.imp.jdbc.exception;

public class ArgumentTypeIllegalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2977431388693050112L;

	public ArgumentTypeIllegalException() {
		super();
	}

	public ArgumentTypeIllegalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArgumentTypeIllegalException(String message) {
		super(message);
	}

	public ArgumentTypeIllegalException(Throwable cause) {
		super(cause);
	}

}
