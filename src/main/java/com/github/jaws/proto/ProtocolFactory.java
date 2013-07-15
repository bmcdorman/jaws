package com.github.jaws.proto;

import java.util.List;

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
	
	public abstract List<Integer> getSupportedVersions();
	
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
	
	private int getLatestSupported(final List<Integer> versions) {
		int latest = -1;
		final List<Integer> supported = getSupportedVersions();
		for(Integer v : versions) {
			if(!supported.contains(latest)) continue;
			if(latest < v) latest = v;
		}
		return latest;
	}
	
	/**
	 * Creates a new incoming stream processor given the WebSocket protocol version.
	 * Automatically detects the highest supported version number and returns the objects
	 * for that version.
	 * 
	 * @param version the WebSocket protocol version (from the opening handshake)
	 * @param role the role this incoming stream should operate under
	 * @return A newly created IncomingStreamProcessor or null if no such version was found
	 */
	public IncomingStreamProcessor createIncomingStreamProcessor(final List<Integer> versions,
			final Role role) {
		final int latest = getLatestSupported(versions);
		if(latest < 0) return null;
		
		return createIncomingStreamProcessor(latest, role);
	}
	
	/**
	 * Creates a new outgoing stream processor given a WebSocket protocol version list.
	 * Automatically detects the highest supported version number and returns the objects
	 * for that version.
	 * 
	 * @param version the WebSocket protocol version (from the opening handshake)
	 * @param role the role this outgoing  stream should operate under
	 * @return A newly created OutgoingStreamProcessor or null if no such version was found
	 */
	public OutgoingStreamProcessor createOutgoingStreamProcessor(final List<Integer> versions,
			final Role role) {
		final int latest = getLatestSupported(versions);
		if(latest < 0) return null;
		
		return createOutgoingStreamProcessor(latest, role);
	}
}
