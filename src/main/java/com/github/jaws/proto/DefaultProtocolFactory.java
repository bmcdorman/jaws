package com.github.jaws.proto;

import com.github.jaws.proto.v13.IncomingStreamProcessorV13;
import com.github.jaws.proto.v13.OutgoingStreamProcessorV13;

/**
 *
 * @author Braden McDorman
 */
public class DefaultProtocolFactory extends ProtocolFactory {
	
	private void checkVersion(int version) {
		if(version < 0) throw new IllegalArgumentException("Version must be non-negative.");
	}
	
	@Override
	public IncomingStreamProcessor createIncomingStreamProcessor(final int version,
			final Role role) {
		checkVersion(version);
		
		if(version == 13) return new IncomingStreamProcessorV13();
		return null;
	}
	
	@Override
	public OutgoingStreamProcessor createOutgoingStreamProcessor(final int version,
			final Role role) {
		checkVersion(version);
		
		if(version == 13) return new OutgoingStreamProcessorV13(role == Role.Client
			? true : false);
		
		return null;
	}
}
