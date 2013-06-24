package com.github.jaws.proto;

/**
 *
 * @author Braden McDorman
 */
public class HandshakeException extends Exception {
	public HandshakeException() {
		super();
	}

	public HandshakeException(String message, Throwable cause) {
		super(message, cause);
	}

	public HandshakeException(String message) {
		super(message);
	}

	public HandshakeException(Throwable cause) {
		super(cause);
	}
}
