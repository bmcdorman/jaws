package com.github.jaws.proto;

/**
 * An incoming stream processor turns raw bytes from an input source (namely a socket) and
 * converts it into a queue of incoming WebSocket messages.
 * 
 * @author Braden McDorman
 */
public abstract class IncomingStreamProcessor {
	public boolean isMessageAvailable() {
		return getNumMessagesAvailable() > 0;
	}
	
	public abstract void admit(final byte[] data, final int offset, final int length);
	
	public abstract int getNumMessagesAvailable();
	public abstract Message nextMessage();
}
