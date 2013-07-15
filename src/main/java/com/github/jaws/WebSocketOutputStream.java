package com.github.jaws;

import com.github.jaws.util.ResizableCircularByteBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * WebSocketOutputStream turns the discrete message protocol of WebSockets into a
 * normal output stream. This is meant to be used in conjunction with the jaws-specific
 * WebSocket transparency protocol and package.
 * 
 * @author Braden McDorman
 */
public class WebSocketOutputStream extends OutputStream {
	private boolean autoPolling;
	private WebSocket webSocket;
	private ResizableCircularByteBuffer outgoing;
	
	WebSocketOutputStream(final WebSocket webSocket) {
		this.webSocket = webSocket;
	}
	
	public void setAutoPolling(final boolean autoPolling) {
		this.autoPolling = autoPolling;
	}
	
	public boolean isAutoPolling() {
		return autoPolling;
	}

	private void autoPoll() throws WebSocketException, IOException {
		if(!autoPolling) return;
		webSocket.poll();
	}
	
	@Override
	public void write(int b) throws IOException {
		outgoing.write((byte)(b & 0xFF));
		
		try {
			autoPoll();
		} catch(WebSocketException e) {
			throw new IOException(e.getCause());
		}
	}
	
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		outgoing.write(buffer, offset, length);
		
		try {
			autoPoll();
		} catch(WebSocketException e) {
			throw new IOException(e.getCause());
		}
	}
	
	
}
