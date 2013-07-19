package com.github.jaws.proto;

import com.github.jaws.util.RandomData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class MessageTest {
	@Test
	public void dataRetained() {
		final Message m = new Message();
		final byte[] data = RandomData.getByteArray(213);
		m.setData(data);
		
		assertEquals("data not retained", data, m.getData());
	}
	
	@Test
	public void typeRetained() {
		final Message m = new Message();
		m.setType(Message.Type.Ping);
		
		assertEquals("type not retained", Message.Type.Ping, m.getType());
	}
	
	@Test
	public void freshlyConstructed() {
		final Message m = new Message();
		assertEquals("non-invalid type", Message.Type.Invalid, m.getType());
		assertNull(m.getData());
	}
}