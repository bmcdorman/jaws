package com.github.jaws;

import java.io.IOException;
import java.io.InputStream;

public class WebSocketInputStream extends InputStream {

	private InputStream inputStream;
	
	WebSocketInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
