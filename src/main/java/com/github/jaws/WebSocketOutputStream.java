package com.github.jaws;

import com.github.jaws.proto.OutgoingStreamProcessor;
import java.io.OutputStream;

/**
 *
 * @author Braden McDorman
 */
public class WebSocketOutputStream extends OutputStream {
	private OutputStream parent;
	
	public WebSocketOutputStream(final OutputStream parent, final OutgoingStreamProcessor out) {
		this.parent = parent;
	}
	
	@Override
	public void write(int b) {
		
	}
}
