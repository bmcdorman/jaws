package com.github.jaws.proto.v13;

import static com.github.jaws.proto.v13.HeaderConstants.*;
import com.github.jaws.util.ResizableCircularByteBuffer;

/**
 * Encapsulates one frame of a larger WebSocket message. One frame object
 * is re-used multiple times to lower the heap allocation performance hit.
 * 
 * @author Braden McDorman
 */
public class DecodedFrame {
	public boolean valid = false;
	public int header = 0x0000;
	public int payloadLength = 0;
	private byte[] mask = { 0, 0, 0, 0 };
	
	// This is by default large enough to hold a non-extended
	// payload length.
	public byte[] data = new byte[126];
	
	/**
	 * Attempts to look ahead in the byte stream and detect if there are enough bytes for
	 * a successful frame decode. This method may return false positives (that is, the decode
	 * can still fail), but should never return true negatives (that is, if isSufficientData
	 * fails, decode will always fail too).
	 * 
	 * @param in The incoming byte stream, in a circular buffer
	 * @return true if a decode might be possible, false otherwise
	 */
	public static boolean isSufficientData(final ResizableCircularByteBuffer in) {
		if(in.available() < 2) return false;
		
		final int header = in.peek(0) << 8 | in.peek(1);
		// Refer to RFC 6455 pg. 29 for more info.
		final int unextendedPayloadSize = header & PAYLOAD_MASK;
		
		int payloadLength = 0;
		int payloadStart = 2;
		if(unextendedPayloadSize < 126) {
			// No extension required
			payloadLength = unextendedPayloadSize;
		} else if(unextendedPayloadSize == 126) {
			// Read next 16 bits as actual size
			if(in.available() < payloadStart + 2) return false;
			
			payloadLength = in.peek(2) << 8 | in.peek(3);
			payloadStart += 2;
		} else {
			// unextendedPayloadSize == 127 (since PAYLOAD_MASK is 7 bits,
			// this is the max). Read next 64 bits as actual size.
			if(in.available() < payloadStart + 8) return false;
			
			long theoreticalPayloadLength =
					in.peek(4) << 56L | in.peek(5) << 48L |
					in.peek(6) << 40L | in.peek(7) << 32L |
					in.peek(8) << 24L | in.peek(9) << 16L |
					in.peek(10) << 8L  | in.peek(11) << 0L;
			
			payloadLength = (int)theoreticalPayloadLength;
			payloadStart += 8;
		}
		
		return in.available() >= payloadLength + payloadStart;
	}
	
	/**
	 * Decodes a single frame from a raw byte source.
	 * 
	 * @param raw A network byte-ordered byte array of a frame.
	 * @param reuse A previous frame object that will be reused for the new
	 * frame.
	 * @return The passed reuse object, unless reuse was null.
	 */
	public static DecodedFrame decode(final ResizableCircularByteBuffer in, DecodedFrame reuse)
			throws DecodeException {
		if(in == null) throw new NullPointerException("Input stream can't be null");
		// Create a new frame if the user passes us null
		if(reuse == null) reuse = new DecodedFrame();
		
		reuse.valid = false;
		
		if(in.available() < 2) {
			throw new DecodeException("Insufficient data to read a complete frame.");
		}
		
		int header = ((in.peek(0) & 0xFF) << 8) | (in.peek(1) & 0xFF);
		
		if((header & (RSV1_BIT | RSV2_BIT | RSV3_BIT)) > 0) {
			final String hex = String.format("%x", header);
			throw new DecodeException("We don't know how to handle frames" +
				" with extension bits. Header is " + hex);
		}
		
		// Refer to RFC 6455 pg. 29 for more info.
		int unextendedPayloadSize = header & PAYLOAD_MASK;
		int payloadStart = 2;
		
		if(unextendedPayloadSize < 126) {
			// No extension required
			reuse.payloadLength = unextendedPayloadSize;

		} else if(unextendedPayloadSize == 126) {
			if(in.available() < 4) {
				throw new DecodeException("Insufficient data to read a complete"
					+ " frame.");
			}
			// Read next 16 bits as actual size
			reuse.payloadLength = (in.peek(2) & 0xFF) << 8 | (in.peek(3) & 0xFF);
	
			payloadStart += 2;
		} else {
			if(in.available() < 10) {
				throw new DecodeException("Insufficient data to read a complete"
					+ " frame.");
			}
			// unextendedPayloadSize == 127 (since PAYLOAD_MASK is 7 bits,
			// this is the max). Read next 64 bits as actual size.
			long theoreticalPayloadLength =
					(in.peek(2) & 0xFF) << 56L | (in.peek(3) & 0xFF) << 48L |
					(in.peek(4) & 0xFF) << 40L | (in.peek(5) & 0xFF) << 32L |
					(in.peek(6) & 0xFF) << 24L | (in.peek(7) & 0xFF) << 16L |
					(in.peek(8) & 0xFF) << 8L  | (in.peek(9) & 0xFF) << 0L;
			
			// We can't easily store this in a byte array
			if(theoreticalPayloadLength > Integer.MAX_VALUE - payloadStart) {
				throw new DecodeException("Frame is too large (greater" +
					" than 31 bits.) While this is tehcnically allowable" +
					" by WebSockets, it is currently unsupported.");
			}
			
			reuse.payloadLength = (int)theoreticalPayloadLength;

			payloadStart += 8;
		}
		
		// Is the mask bit set?
		// See pg. 33
		if((header & MASK_BIT) > 0) {
			for(int i = 0; i < reuse.mask.length; ++i) {
				reuse.mask[i] = (byte)in.peek(i + payloadStart);
			}
			payloadStart += reuse.mask.length;
		} else {
			for(int i = 0; i < reuse.mask.length; ++i) reuse.mask[i] = 0;
		}
		
		if(in.available() < payloadStart + reuse.payloadLength) {
			throw new DecodeException("Insufficient data to read a complete"
					+ " frame.");
		}
		
		in.discard(payloadStart);
		
		// FIXME: This could get really large and next frame it would stay
		// that size.
		if(reuse.payloadLength > reuse.data.length) {
			reuse.data = new byte[reuse.payloadLength];
		}
		
		byte[] data = reuse.data;
		
		if(reuse.payloadLength > 0 && in.read(data, 0, reuse.payloadLength) < 0) {
			throw new DecodeException("Insufficient data available to decode frame.");
		}
		
		for(int j = 0; j < reuse.payloadLength; ++j) {
			// mask is zeroed out in case MASK_BIT isn't set
			data[j] = (byte)(data[j] ^ reuse.mask[j % 4]);
		}
		
		reuse.header = header;
		reuse.valid = true;
		
		return reuse;
	}
}
