package com.github.jaws.proto.v13;

import static com.github.jaws.proto.v13.HeaderConstants.*;
import java.io.IOException;
import java.io.InputStream;

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
	
	// This is by default large enough to hold a non-extended
	// payload length.
	public byte[] data = new byte[126];
	
	private static byte readByte(final InputStream in) throws DecodeException, IOException {
		final int val = in.read();
		if(val < 0) {
			throw new DecodeException("Insufficient data available to decode frame.");
		}
		return (byte)val;
	}
	
	/**
	 * Decodes a single frame from a raw byte source.
	 * 
	 * @param raw A network byte-ordered byte array of a frame.
	 * @param reuse A previous frame object that will be reused for the new
	 * frame.
	 * @return The passed reuse object, unless reuse was null.
	 */
	public static DecodedFrame decode(final InputStream in, DecodedFrame reuse)
			throws DecodeException, IOException {
		// Create a new frame if the user passes us null
		if(reuse == null) reuse = new DecodedFrame();
		
		reuse.valid = false;
		
		int header = (((readByte(in) & 0xFF) << 8) | (readByte(in) & 0xFF));
		
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
			// Read next 16 bits as actual size
			reuse.payloadLength = (readByte(in) & 0xFF) << 8 | (readByte(in) & 0xFF);
			payloadStart += 2;
		} else {
			// unextendedPayloadSize == 127 (since PAYLOAD_MASK is 7 bits,
			// this is the max). Read next 64 bits as actual size.
			long theoreticalPayloadLength =
					(readByte(in) & 0xFF) << 56L | (readByte(in) & 0xFF) << 48L |
					(readByte(in) & 0xFF) << 40L | (readByte(in) & 0xFF) << 32L |
					(readByte(in) & 0xFF) << 24L | (readByte(in) & 0xFF) << 16L |
					(readByte(in) & 0xFF) << 8L  | (readByte(in) & 0xFF) << 0L;
			
			// We can't easily store this in a byte array
			if(theoreticalPayloadLength > Integer.MAX_VALUE - payloadStart) {
				throw new DecodeException("Frame is too large (greater" +
					" than 31 bits. While this is tehcnically allowable" +
					" by WebSockets, it is currently unsupported.");
			}
			
			reuse.payloadLength = (int)theoreticalPayloadLength;
			payloadStart += 8;
		}
		
		// Is the mask bit set?
		// See pg. 33
		byte[] mask = { 0, 0, 0, 0 };
		if((header & MASK_BIT) > 0) {
			for(int i = 0; i < mask.length; ++i) {
				mask[i] = readByte(in);
			}
			payloadStart += mask.length;
		}
		
		// FIXME: This could get really large and next frame it would stay
		// that size.
		if(reuse.payloadLength > reuse.data.length) {
			reuse.data = new byte[reuse.payloadLength];
		}
				
		byte[] data = reuse.data;
		
		if(in.read(data, 0, reuse.payloadLength) < 0) {
			throw new DecodeException("Insufficient data available to decode frame.");
		}
		
		for(int j = 0; j < reuse.payloadLength; ++j) {
			// mask is zeroed out in case MASK_BIT isn't set
			data[j] = (byte)(data[j] ^ mask[j % 4]);
		}
		
		reuse.header = header;
		reuse.valid = true;
		
		return reuse;
	}
}
