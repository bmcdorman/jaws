package com.github.jaws.transparency;

import com.github.jaws.WebSocket;
import com.github.jaws.WebSocketException;
import com.github.jaws.proto.Message;
import com.github.jaws.proto.Message.Type;
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
	private boolean autoPolling = false;
	private WebSocket webSocket;
	private ResizableCircularByteBuffer outgoing = new ResizableCircularByteBuffer(1024);
	
	public WebSocketOutputStream(final WebSocket webSocket) {
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
		
		if(!outgoing.isEmpty()) {
			// TODO: This is a lot of heap allocation...
			Message out = new Message();
			out.setType(Type.Binary);
			byte[] buffer = new byte[outgoing.available()];
			outgoing.read(buffer);
			out.setData(buffer);
			webSocket.send(out);
		}
		
		webSocket.poll();
	}
	
	@Override
	public void write(int b) throws IOException {
		outgoing.write((byte)(b & 0xFF));
		
		try {
			autoPoll();
		} catch(WebSocketException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		outgoing.write(buffer, offset, length);
		
		try {
			autoPoll();
		} catch(WebSocketException e) {
			throw new IOException(e);
		}
	}
	
	
}
