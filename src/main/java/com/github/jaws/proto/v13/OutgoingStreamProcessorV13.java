package com.github.jaws.proto.v13;

import com.github.jaws.proto.Message;
import com.github.jaws.proto.OutgoingStreamProcessor;
import com.github.jaws.util.ResizableCircularByteBuffer;

/**
 *
 * @author Braden McDorman
 */
public class OutgoingStreamProcessorV13 extends OutgoingStreamProcessor {
	private ResizableCircularByteBuffer out = new ResizableCircularByteBuffer(1024);
	private EncodedFrame frame;
	
	private boolean masked;
	
	public OutgoingStreamProcessorV13(final boolean masked) {
		this.masked = masked;
	}
	
	@Override
	public int nextBytes(final byte[] data, int offset, int length) {
		return out.read(data, offset, length);
	}

	
	@Override
	public void admit(final Message message) {
		try {
			frame = EncodedFrame.encode(MessageHelpers.getOpcode(message.getType()),
				true, masked, message.getData(), 0, message.getDataLength(), frame);
			out.write(frame.raw, 0, frame.totalLength);
		} catch(EncodeException e) {
			System.err.println("FIXME: Need to raise an additional exception.");
		}
	}
}
