package com.github.jaws;

import java.util.List;

/**
 * During a WebSocket handshake, the client sends the server a
 * list of supported protocols. It is the server's job to select
 * the preferred protocol and send that information back to the
 * potential peer. ServerProtocolSelector facilitates this
 * behavior through a callback that is called during handshakes.
 * 
 * @author Braden McDorman
 */
public abstract class ServerProtocolSelector {
	
	/**
	 * Select the connection's protocol from a list of supported client protocols
	 * 
	 * @param protocols The list of protocols a potential client supports. This list MAY
	 * be empty, but will never be null.
	 * @return The selected protocol, or empty for no specified protocol, or null for error
	 */
	public abstract String selectProtocol(final List<String> protocols);
}
