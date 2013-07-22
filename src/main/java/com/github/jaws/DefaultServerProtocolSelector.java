package com.github.jaws;

import java.util.List;

/**
 * The default implementation of the ServerProtocolSelector. This
 * implementation always chooses the "empty" WebSocket protocol,
 * which is the default when the client doesn't specify supported protocols.
 * 
 * @author Braden McDorman
 */
public class DefaultServerProtocolSelector extends ServerProtocolSelector {

	@Override
	public String selectProtocol(final List<String> protocols) {
		if(protocols == null) throw new NullPointerException("Protocols list was null.");
		// Select the "empty" or default protocol
		return "";
	}
	
}
