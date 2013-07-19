package com.github.jaws.transparency;

import com.github.jaws.WebSocket;
import com.github.jaws.WebSocketException;
import com.github.jaws.proto.Message;
import com.github.jaws.util.ResizableCircularByteBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * WebSocketInputStream turns the discrete message protocol of WebSockets into a
 * normal input stream. This is meant to be used in conjunction with the jaws-specific
 * WebSocket transparency protocol and package.
 * 
 * @author Braden McDorman
 */
public class WebSocketInputStream extends InputStream {
	private boolean autoPolling;
	private WebSocket webSocket;
	private ResizableCircularByteBuffer incoming = new ResizableCircularByteBuffer(1024);
	
	public WebSocketInputStream(final WebSocket webSocket) {
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
	
	private void ensureData(final int length) throws WebSocketException {
		if(incoming.available() >= length) return;
		Message m = null;
		while(incoming.available() < length && (m = webSocket.recv()) != null) {
			incoming.write(m.getData(), 0, m.getDataLength());
		}
	}
	
	@Override
	public int available() {
		try {
			autoPoll();
			// Try and get as much data as possible
			ensureData(Integer.MAX_VALUE);
		} catch(Exception e) {
			// Silent error
		}
		
		return incoming.available();
	}
	
	@Override
	public int read() throws IOException {
		try {
			autoPoll();
			ensureData(1);
		} catch(WebSocketException e) {
			throw new IOException(e.getCause());
		}
		
		return incoming.read();
	}
	
	@Override
	public int read(byte[] buffer) throws IOException {
		try {
			autoPoll();
			ensureData(buffer.length);
		} catch(WebSocketException e) {
			throw new IOException(e.getCause());
		}
		
		return incoming.read(buffer);
	}
	
	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		try {
			autoPoll();
			ensureData(length);
		} catch(WebSocketException e) {
			throw new IOException(e.getCause());
		}
		
		return incoming.read(buffer, offset, length);
	}
	
	
}
