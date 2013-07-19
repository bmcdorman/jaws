package com.github.jaws.proto.v13;


import com.github.jaws.util.RandomData;
import com.github.jaws.util.ResizableCircularByteBuffer;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class DecodedFrameTest {
	@Test(expected = NullPointerException.class)
	public void nullInputStream() throws DecodeException, IOException {
		DecodedFrame.decode(null, null);
	}
	
	@Test(expected = DecodeException.class)
	public void prematureEof() throws DecodeException, IOException {
		final byte[] someData = RandomData.getByteArray(2);
		final ResizableCircularByteBuffer in = new ResizableCircularByteBuffer(someData);
		DecodedFrame.decode(in, null);
	}
}
