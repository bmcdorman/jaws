package com.github.jaws.proto;

/**
 * jaws separates its various WebSocket backends based off of WebSocket protocol version.
 * A ProtocolFactory is the link between the various WebSocket backends and
 * the jaws front-end.
 * 
 * @author Braden McDorman
 */
public abstract class ProtocolFactory {
	public enum Role {
		Server,
		Client
	}
	
	/**
	 * Creates a new incoming stream processor given the WebSocket protocol version.
	 * 
	 * @param version the WebSocket protocol version (from the opening handshake)
	 * @param role the role this incoming stream should operate under
	 * @return A newly created IncomingStreamProcessor or null if no such version was found
	 */
	public abstract IncomingStreamProcessor createIncomingStreamProcessor(final int version,
		final Role role);
	
	/**
	 * Creates a new outgoing stream processor given the WebSocket protocol version.
	 * 
	 * @param version the WebSocket protocol version (from the opening handshake)
	 * @param role the role this outgoing  stream should operate under
	 * @return A newly created OutgoingStreamProcessor or null if no such version was found
	 */
	public abstract OutgoingStreamProcessor createOutgoingStreamProcessor(final int version,
		final Role role);
}
