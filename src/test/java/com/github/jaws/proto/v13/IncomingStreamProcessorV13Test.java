package com.github.jaws.proto.v13;

import static org.junit.Assert.*;

import com.github.jaws.http.HttpHeader;
import com.github.jaws.proto.Message;
import com.github.jaws.util.RandomData;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class IncomingStreamProcessorV13Test {
	@Test
	public void constructor() {
		IncomingStreamProcessorV13 sp = new IncomingStreamProcessorV13();
		assertFalse("message was available after construction", sp.isMessageAvailable());
		assertEquals("message count was unequal to 0", 0, sp.getNumMessagesAvailable());
		assertNull("message return wasn't null", sp.nextMessage());
	}
	
	@Test
	public void admitMaskedSingleFrame() throws EncodeException {
		final byte[] data = RandomData.getByteArray(512);
		EncodedFrame frame = null;
		frame = EncodedFrame.encode(HeaderConstants.BINARY_FRAME_OPCODE,
			true, true, data, 0, data.length, frame);
		IncomingStreamProcessorV13 sp = new IncomingStreamProcessorV13();
		sp.admit(frame.raw, 0, frame.totalLength);
		assertEquals("more or less than one frame available", 1,
			sp.getNumMessagesAvailable());
		assertTrue("Message available was false", sp.isMessageAvailable());
		Message m = sp.nextMessage();
		assertNotNull("Message was null", m);
		assertEquals("Data length was incorrect", data.length, m.getDataLength());
		final byte[] actualData = new byte[m.getDataLength()];
		System.arraycopy(m.getData(), 0, actualData, 0, m.getDataLength());
		assertArrayEquals("data unequal", data, actualData);
	}
	
	@Test
	public void admitUnmaskedSingleFrame() throws EncodeException {
		final byte[] data = RandomData.getByteArray(512);
		EncodedFrame frame = null;
		frame = EncodedFrame.encode(HeaderConstants.BINARY_FRAME_OPCODE,
			true, false, data, 0, data.length, frame);
		IncomingStreamProcessorV13 sp = new IncomingStreamProcessorV13();
		sp.admit(frame.raw, 0, frame.totalLength);
		/*assertEquals("frame shoudn't be available", 0, sp.getNumMessagesAvailable());
		assertFalse("Message available was true", sp.isMessageAvailable());
		assertNull("message wasn't null", sp.nextMessage());*/
	}
	
	@Test
	public void admitMaskedFrames() throws EncodeException {
		EncodedFrame frame = null;
		IncomingStreamProcessorV13 sp = new IncomingStreamProcessorV13();
		
		final byte[][] dataFrames = new byte[3][];
		dataFrames[0] = RandomData.getByteArray(1024);
		dataFrames[1] = RandomData.getByteArray(231);
		dataFrames[2] = RandomData.getByteArray(4132);
		
		frame = EncodedFrame.encode(HeaderConstants.BINARY_FRAME_OPCODE,
			false, true, dataFrames[0], 0, dataFrames[0].length, frame);
		sp.admit(frame.raw, 0, frame.totalLength);
		
		frame = EncodedFrame.encode(HeaderConstants.CONTINUATION_FRAME_OPCODE,
			false, true, dataFrames[1], 0, dataFrames[1].length, frame);
		sp.admit(frame.raw, 0, frame.totalLength);
		
		frame = EncodedFrame.encode(HeaderConstants.CONTINUATION_FRAME_OPCODE,
			true, true, dataFrames[2], 0, dataFrames[2].length, frame);

		sp.admit(frame.raw, 0, frame.totalLength);
		
		assertEquals("more or less than one frame available", 1,
			sp.getNumMessagesAvailable());
		assertTrue("Message available was false", sp.isMessageAvailable());
		
		Message m = sp.nextMessage();
		assertNotNull("Message was null", m);
		
		final byte[] expectedData = new byte[dataFrames[0].length
			+ dataFrames[1].length + dataFrames[2].length];
		System.arraycopy(dataFrames[0], 0, expectedData, 0, dataFrames[0].length);
		System.arraycopy(dataFrames[1], 0, expectedData, dataFrames[0].length,
			dataFrames[1].length);
		System.arraycopy(dataFrames[2], 0, expectedData, dataFrames[0].length
			+ dataFrames[1].length, dataFrames[2].length);
		assertEquals("Data length was incorrect", expectedData.length, m.getDataLength());
		final byte[] actualData = new byte[m.getDataLength()];
		
		System.arraycopy(m.getData(), 0, actualData, 0, m.getDataLength());
		assertArrayEquals("data unequal", expectedData, actualData);
	}
}
