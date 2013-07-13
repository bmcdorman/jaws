package com.github.jaws.proto;

import java.io.IOException;
import java.io.InputStream;

/**
 * An incoming stream processor turns raw bytes from an input source (namely a socket) and
 * converts it into a queue of incoming WebSocket messages.
 * 
 * @author Braden McDorman
 */
public abstract class IncomingStreamProcessor {
	private byte[] buffer = new byte[1024];
	
	public boolean isMessageAvailable() {
		return getNumMessagesAvailable() > 0;
	}
	
	public void read(final InputStream in) throws IOException {
		while(in.available() > 0) {
			final int l = in.read(buffer, 0, Math.min(buffer.length, in.available()));
			admit(buffer, 0, l);
		}
	}
	
	public abstract void admit(final byte[] data, final int offset, final int length);
	
	public abstract int getNumMessagesAvailable();
	public abstract Message nextMessage();
}
