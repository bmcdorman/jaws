package com.github.jaws;

/**
 *
 * @author Braden McDorman
 */
public class WebSocketException extends Exception {
	public WebSocketException() {
		super();
	}

	public WebSocketException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public WebSocketException(final String message) {
		super(message);
	}

	public WebSocketException(final Throwable cause) {
		super(cause);
	}
}
