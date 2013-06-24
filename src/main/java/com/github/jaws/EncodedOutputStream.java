package com.github.jaws;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import com.github.jaws.proto.v13.EncodedFrame;
import com.github.jaws.proto.v13.HeaderConstants;

public class EncodedOutputStream extends BufferedOutputStream {
	private EncodedFrame reuse;
	
	EncodedOutputStream(OutputStream out) {
		super(out);
	}
	
	@Override
	public void flush()
	{
		/*reuse = EncodedFrame.encode(HeaderConstants.BINARY_FRAME_OPCODE, true,
				true, buf, reuse);
		count = 0;*/
	}
}
