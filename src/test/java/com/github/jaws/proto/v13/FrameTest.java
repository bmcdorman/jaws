package com.github.jaws.proto.v13;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.github.jaws.proto.v13.HeaderConstants.*;
import com.github.jaws.util.RandomData;
import java.io.ByteArrayInputStream;

@RunWith(JUnit4.class)
public class FrameTest {
	
	
	@Test
	public void inverseProperty() throws DecodeException, EncodeException, java.io.IOException {
		final byte[] data = RandomData.getByteArray(128);
		final boolean masked = true;
		EncodedFrame encodedFrame = null;
		DecodedFrame decodedFrame = null;
		
		encodedFrame = EncodedFrame.encode(TEXT_FRAME_OPCODE, false, masked,
			data, 0, data.length, encodedFrame);
		assertTrue("encode valid?", encodedFrame.valid);
		
		final ByteArrayInputStream in = new ByteArrayInputStream(encodedFrame.raw);
		decodedFrame = DecodedFrame.decode(in, decodedFrame);
		assertTrue("decode valid?", decodedFrame.valid);
		assertEquals(decodedFrame.payloadLength, data.length);
		assertTrue("decoded opcode correct?", (decodedFrame.header & TEXT_FRAME_OPCODE) != 0);
		assertTrue("decoded data masked?", masked
			? (decodedFrame.header & MASK_BIT) != 0
			: (decodedFrame.header & MASK_BIT) == 0);
		
		// We can't use junit's assertArrayEquals since byte array lengths don't necessarily
		// match the data lengths.
		boolean dataMatches = true;
		for(int i = 0; i < data.length; ++i) {
			dataMatches &= decodedFrame.data[i] == data[i];
		}
		assertTrue("Data match", dataMatches);
	}
}