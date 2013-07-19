package com.github.jaws.proto;

import com.github.jaws.util.RandomData;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class HandshakeTest  {
	@Test(expected = NullPointerException.class)
	public void setNullKey() {
		final Handshake h = new Handshake();
		h.setKey(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidKey1() {
		final byte[] key = RandomData.getByteArray(17);
		final Handshake h = new Handshake();
		h.setKey(key);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidKey2() {
		final byte[] key = RandomData.getByteArray(3);
		Handshake h = new Handshake();
		h.setKey(key);
	}
	
	@Test(expected = NullPointerException.class)
	public void setNullExtensions() {
		final Handshake h = new Handshake();
		final List<String> extensions = new ArrayList<String>();
		extensions.add("test");
		extensions.add(null);
		h.setExtensions(extensions);
	}
	
	@Test(expected = NullPointerException.class)
	public void addNullExtension() {
		Handshake h = new Handshake();
		h.addExtension(null);
	}
	
	@Test
	public void extensionsRetained() {
		final Handshake h = new Handshake();
		final List<String> extensions = new ArrayList<String>();
		extensions.add("a");
		h.setExtensions(extensions);
		h.addExtension("b");
		h.addExtension("c");
		
		final List<String> expected = new ArrayList<String>();
		expected.add("a");
		expected.add("b");
		expected.add("c");
		
		assertEquals("Extensions not retained", expected, h.getExtensions());
	}
	
	@Test
	public void noExtensionsOnConstruction() {
		final Handshake h = new Handshake();
		assertEquals("Extensions after construction", 0, h.getExtensions().size());
	}
	
	@Test
	public void nullKeyOnConstruction() {
		final Handshake h = new Handshake();
		assertNull("Non-null key after construction", h.getKey());
	}
}
