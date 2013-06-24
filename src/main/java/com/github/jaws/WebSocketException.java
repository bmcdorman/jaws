package com.github.jaws;

import java.io.IOException;

public class WebSocketException extends IOException {
	private static final long serialVersionUID = 8228094561297996603L;
	
	public WebSocketException() {
		super();
	}

	public WebSocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebSocketException(String message) {
		super(message);
	}

	public WebSocketException(Throwable cause) {
		super(cause);
	}
}
