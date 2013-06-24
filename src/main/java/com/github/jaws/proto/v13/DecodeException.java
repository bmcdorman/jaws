package com.github.jaws.proto.v13;

import java.io.IOException;

public class DecodeException extends IOException {
	private static final long serialVersionUID = -1774176857564824347L;

	public DecodeException() {
		super();
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodeException(String message) {
		super(message);
	}

	public DecodeException(Throwable cause) {
		super(cause);
	}
}
