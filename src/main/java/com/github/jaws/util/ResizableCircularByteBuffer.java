package com.github.jaws.util;

import java.io.IOException;
import java.io.OutputStream;

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
	
	/**
	 * Creates a new ResizableCircularByteBuffer with the contents of the given
	 * array.
	 * 
	 * @param data The initial data to populate this buffer with
	 */
	public ResizableCircularByteBuffer(final byte[] data) {
		this(data.length);
		write(data);
	}
	
	/**
	 * Clears the contents of the buffer by resetting position variables
	 */
	public void clear() {
		start = end = size = 0;
	}
	
	/**
	 * Modify the default or previously set scale factor
	 * when the buffer needs to be resized. This multiplicative
	 * factor adjusts the extra memory to allocate when presented
	 * with an out-of-memory condition.
	 * 
	 * @param scaleFactor A double 
	 */
	public void setScaleFactor(final double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	
	public double getScaleFactor() {
		return scaleFactor;
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
		
		byte[] oldBytes = null;
		if(size > 0) {
			// FIXME: We could avoid creating this temporary buffer
			oldBytes = new byte[size];
			read(oldBytes, 0, oldBytes.length);
		}
		
		// Increase the buffer's size by ceil(length * scaleFactor)
		buffer = new byte[(int)Math.ceil(length * scaleFactor)];
		
		start = end = size = 0;
		
		if(oldBytes != null) {
			end = oldBytes.length;
			System.arraycopy(oldBytes, 0, buffer, 0, oldBytes.length);
			size = oldBytes.length;
		}
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
	 * Looks ahead a single byte from the circular buffer.
	 * 
	 * @return The next readable byte
	 */
	public int peek() {
		return peek(0);
	}
	
	/**
	 * Looks ahead an arbitrary number of bytes in the buffer.
	 * 
	 * @return The next readable byte
	 */
	public int peek(final int offset) {
		if(offset >= size) return -1;
		
		return buffer[(start + offset) % buffer.length] & 0xFF;
	}
	
	/**
	 * 
	 * @param data The array to peek data into
	 * @return The number of bytes actually peeked into the array
	 */
	public int peek(final byte[] data) {
		return peek(data, 0, data.length);
	}
	
	/**
	 * Looks ahead several bytes from the circular buffer. This method is faster
	 * than peeking bytes one-by-one using peek()
	 * 
	 * @param data The array to populate with bytes from the circular buffer
	 * @param offset Specify the offset at which to begin placing bytes into data.
	 * @param length The maximum number of bytes to peek into data.
	 * 
	 * @return The actual number of bytes peeked into data at offset
	 */
	public int peek(final byte[] data, final int offset, final int length) {
		if(offset < 0) {
			throw new IllegalArgumentException("Offset can't be negative (was "
				+ offset + ")");
		}
		if(length <= 0) {
			throw new IllegalArgumentException("Length must be positive (was "
				+ length + ")");
		}
		
		int actualLength = Math.min(length, size);
		if(actualLength <= 0) return 0;
		
		if(actualLength + offset > data.length) {
			// Error: Given array isn't large enough
		}
		
		// Since the buffer is circular, we have to handle the
		// case in which the buffer is on two sides of the array
		if(start > end && start + actualLength >= buffer.length) {
			final int endPartBytes = buffer.length - start;
			System.arraycopy(buffer, start, data, offset, endPartBytes);
			final int startPartBytes = actualLength - endPartBytes;
			if(startPartBytes > 0) {
				System.arraycopy(buffer, 0, data, offset
					+ endPartBytes, startPartBytes);
			}
		} else {
			System.arraycopy(buffer, start, data, offset, actualLength);
		}
		
		return actualLength;
	}
	
	/**
	 * Reads a single byte from the circular buffer.
	 * 
	 * @return The next readable byte
	 */
	public int read() {
		final int ret = peek();
		if(ret < 0) return ret;
		
		++start;
		start %= buffer.length;
		--size;
		
		return ret;
	}
	
	public int read(final byte[] data) {
		return read(data, 0, data.length);
	}
	
	/**
	 * Reads several bytes from the circular buffer. This method is faster
	 * than reading bytes one-by-one using read()
	 * 
	 * @param data The array to populate with bytes from the circular buffer
	 * @param offset Specify the offset at which to begin placing bytes into data.
	 * @param length The maximum number of bytes to read into data.
	 * 
	 * @return The actual number of bytes read into data at offset
	 */
	public int read(final byte[] data, final int offset, final int length) {
		final int actualLength = peek(data, offset, length);
		
		start += actualLength;
		start %= buffer.length;
		size -= actualLength;
		
		return actualLength;
	}
	
	/**
	 * Discard one or more bytes from the circular buffer's head
	 * 
	 * @return The number of bytes actually discarded
	 */
	public int discard(final int length) {
		if(length <= 0) {
			throw new IllegalArgumentException("Length must be positive (was "
				+ length + ")");
		}
		final int actualLength = Math.min(length, size);
		start += actualLength;
		start %= buffer.length;
		size -= actualLength;
		return actualLength;
	}
	
	public int transfer(final ResizableCircularByteBuffer to, final int length) {
		final int actualLength = Math.min(size, length);
		to.ensureCapacity(to.size + length);
		
		if(to.buffer.length < end + length) {
			final int endPartBytes = to.buffer.length - end;
			read(to.buffer, end, endPartBytes);
			final int startPartBytes = length - endPartBytes;
			read(to.buffer, 0, startPartBytes);
		} else {
			read(to.buffer, end, length);
		}
		
		return actualLength;
	}
	
	public void debug(final OutputStream in) throws IOException {
		in.write(buffer, 0, buffer.length);
		System.err.println("Start = " + start);
		System.err.println("End = " + end);
	}
}
