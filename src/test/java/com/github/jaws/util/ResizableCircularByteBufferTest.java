package com.github.jaws.util;

import java.util.Arrays;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * 
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class ResizableCircularByteBufferTest {
	@Test
	public void fifo() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		
		// Write 0..9
		for(int i = 0; i < 10; ++i) b.write((byte)i);
		
		// Read back 0..9
		for(int i = 0; i < 10; ++i) {
			assertEquals("Unexpected element", i, (int)b.read());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidStartingSize1() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidStartingSize2() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(-123);
	}
	
	@Test
	public void freshlyConstructed() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		assertEquals("Available not zero after construction", 0, b.available());
		assertTrue("Is empty not true after construction", b.isEmpty());
	}
	
	@Test
	public void addByte() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		b.write((byte)12);
		assertEquals("Available incorrect", 1, b.available());
		assertFalse("buffer reported to be empty", b.isEmpty());
		assertEquals("Content mismatch", (byte)12, b.read());
	}
	
	@Test
	public void addBytes() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		final byte[] bytes = RandomData.getByteArray(500);
		b.write(bytes);
		assertEquals("Available incorrect", bytes.length, b.available());
		assertFalse("buffer reported to be empty", b.isEmpty());
		for(final byte i : bytes) assertEquals("Contents mismatch", i & 0xFF, b.read());
	}
	
	@Test
	public void removeFromEmpty() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		assertEquals(-1, b.read());
	}
	
	@Test
	public void peekAhead() {
		final byte[] bytes = RandomData.getByteArray(500);
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(bytes);
		
		assertEquals(bytes[0] & 0xFF, b.peek());
		assertEquals(bytes[4] & 0xFF, b.peek(4));
		assertEquals(bytes[402] & 0xFF, b.peek(402));
	}
	
	@Test
	public void peekAheadTooMany() {
		final byte[] bytes = RandomData.getByteArray(500);
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(bytes);
		
		// Peek one byte over data length
		assertEquals("expected invalid value for invalid range", -1, b.peek(500));
		
		// Peek several bytes over data length
		assertEquals("expected invalid value for invalid range", -1, b.peek(5123));
	}
	
	/**
	 * This test is meant to invoke the circular nature of the buffer
	 */
	@Test
	public void massageImpl() {
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(5);
		
		final byte[] firstBytes = RandomData.getByteArray(500);
		b.write(firstBytes);
		
		
		final byte[] readSome = new byte[400];
		assertEquals(readSome.length, b.read(readSome));
		for(int i = 0; i < readSome.length; ++i) {
			assertEquals("Content mismatch", firstBytes[i], readSome[i]);
		}
		
		
		final byte[] secondBytes = RandomData.getByteArray(200);
		b.write(secondBytes);
		
		
		final byte[] someMore = new byte[200];
		assertEquals(someMore.length, b.read(someMore));
		for(int i = 0, j = readSome.length; i < someMore.length; ++i, ++j) {
			if(j < firstBytes.length) {
				assertEquals("Content mismatch", firstBytes[j] & 0xFF,
					someMore[i] & 0xFF);
			} else {
				assertEquals("Content mismatch", secondBytes[j % firstBytes.length]
					& 0xFF, someMore[i] & 0xFF);
			}
		}
		
		assertEquals("expected number of bytes left", firstBytes.length + secondBytes.length
			- readSome.length - someMore.length, b.available());
	}
	
	@Test
	public void clear() {
		final byte[] bytes = RandomData.getByteArray(500);
		final ResizableCircularByteBuffer b = new ResizableCircularByteBuffer(bytes);
		b.clear();
		assertTrue("Cleared buffer", b.isEmpty());
		assertEquals("Cleared buffer", 0, b.available());
	}
}
