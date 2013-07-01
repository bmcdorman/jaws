package com.github.jaws.proto;

/**
 * An outgoing stream processor turns discrete WebSocket messages into a raw byte stream
 * for sending over an output stream (namely a socket)
 * 
 * @author Braden McDorman
 */
public abstract class OutgoingStreamProcessor {
	/**
	 * nextBytes gets the next bytes to be written out. Call this often
	 * and send it over the output stream.
	 * 
	 * @param data the data array to read into
	 * @param offset the offset at which to read into
	 * @param length the maximum number of bytes to read in
	 * @return the number of bytes actually read into the array
	 */
	public abstract int nextBytes(final byte[] data, final int offset, final int length);
	
	/**
	 * 
	 * @param data The data array to read into
	 * @return the number of bytes actually read into the array
	 */
	public int nextBytes(final byte[] data) {
		return nextBytes(data, 0, data.length);
	}
	
	/**
	 * Admits a message to be converted to a raw byte stream and written out.
	 * 
	 * @param message the message to admit
	 * @return true if successfully admitted, false otherwise
	 */
	public abstract boolean admit(final Message message);
}
