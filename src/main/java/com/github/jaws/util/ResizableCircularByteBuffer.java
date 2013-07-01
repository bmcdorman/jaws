package com.github.jaws.util;

import java.io.IOException;
import java.util.Arrays;

/**
 * A fast re-sizable circular buffer implementation. This implementation is not thread safe.
 * 
 * @author Braden McDorman
 */
public final class ResizableCircularByteBuffer {
	private final static double DEFAULT_SCALE_FACTOR = 1.5;
	
	private byte[] buffer;
	private int start;
	private int end;
	
	// This is kept simply because it avoids
	// needless branches all over the place
	private int size;
	
	private double scaleFactor = DEFAULT_SCALE_FACTOR;
	
	/**
	 * Creates a new circular buffer with the specified starting size
	 * 
	 * @param startingSize the initial size of the buffer. The implementation will then
	 * expand or shrink this value depending on the buffer's contents
	 */
	public ResizableCircularByteBuffer(final int startingSize) {
		if(startingSize <= 0) throw new IllegalArgumentException("Starting size must be"
			+ " greater than 0");
		buffer = new byte[startingSize];
	}
	
	public ResizableCircularByteBuffer(final byte[] data) {
		this(data.length);
		write(data);
	}
	
	/**
	 * 
	 * @param data
	 * @param offset
	 * @param length 
	 */
	public void write(final byte[] data, final int offset, final int length) {
		ensureCapacity(size + length);
		
		if(end + length > buffer.length) {
			final int endPartBytes = buffer.length - end;
			System.arraycopy(data, offset, buffer, end, endPartBytes);
			final int startPartBytes = length - endPartBytes;
			System.arraycopy(data, offset + endPartBytes, buffer, 0, startPartBytes);
		} else {
			System.arraycopy(data, offset, buffer, end, length);
		}
		
		end += length;
		end %= buffer.length;
		size += length;
	}
	
	/**
	 * Writes an entire array to the end of the buffer
	 * 
	 * @param data the array to write to the buffer
	 */
	public void write(final byte[] data) {
		write(data, 0, data.length);
	}
	
	/**
	 * Writes a single byte to the end of the buffer
	 * 
	 * @param b the byte to write to the end of the buffer
	 */
	public void write(final byte b) {
		ensureCapacity(size + 1);
		
		buffer[end] = b;
		
		++end;
		end %= buffer.length;
		++size;
	}
	
	private void ensureCapacity(int length) {
		if(buffer.length >= length) return;
		
		byte[] oldBytes = new byte[size];
		read(oldBytes, 0, oldBytes.length);
		// Increase the buffer's size by ceil(length * scaleFactor)
		buffer = new byte[(int)Math.ceil(length * scaleFactor)];
		
		start = 0;
		end = oldBytes.length;
		System.arraycopy(oldBytes, 0, buffer, 0, oldBytes.length);
		size = oldBytes.length;
	}
	
	/**
	 * 
	 * @return true if the buffer is empty, false otherwise
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * @return the number of bytes available for reading
	 */
	public int available() {
		return size;
	}
	
	/**
	 * Reads a single byte from the circular queue.
	 * 
	 * @return The next readable byte
	 */
	public int read() {
		if(isEmpty()) return -1;
		
		final byte ret = buffer[start];
		
		++start;
		start %= buffer.length;
		--size;
		
		return ret & 0xFF;
	}
	
	public int read(final byte[] data) {
		return read(data, 0, data.length);
	}
	
	/**
	 * Reads several bytes from the circular queue. This method is faster
	 * than reading bytes one-by-one using read()
	 * 
	 * @param data The array to populate with bytes from the circular buffer
	 * @param offset Specify the offset at which to begin placing bytes into data.
	 * @param length The maximum number of bytes to read into data.
	 * 
	 * @return The actual number of bytes read into data at offset
	 */
	public int read(final byte[] data, final int offset, final int length) {
		int actualLength = Math.min(length, size);
		if(actualLength <= 0) return 0;
		
		if(actualLength + offset > data.length) {
			// Error: Given array isn't large enough
		}
		
		// Since the buffer is circular, we have to handle the
		// case in which the buffer is on two sides of the array
		if(start < end) {
			final int endPartBytes = Math.min(buffer.length - start, actualLength);
			System.arraycopy(buffer, start, data, offset, endPartBytes);
			final int startPartBytes = actualLength - endPartBytes;
			if(startPartBytes > 0) {
				System.arraycopy(buffer, 0, data, offset
					+ endPartBytes, startPartBytes);
			}
		} else {
			System.arraycopy(buffer, start, data, offset, actualLength);
		}
		
		start += actualLength;
		start %= buffer.length;
		size -= actualLength;
		
		return actualLength;
	}
}
