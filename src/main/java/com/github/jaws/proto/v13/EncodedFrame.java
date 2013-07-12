package com.github.jaws.proto.v13;

import static com.github.jaws.proto.v13.HeaderConstants.*;

public class EncodedFrame {
	public boolean valid = false;
	public byte[] raw = new byte[126];
	public int payloadLength = 0;
	public int payloadStart = 0;
	public int totalLength = 0;
	private	byte[] payloadExtra = { 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public static EncodedFrame encode(final int opcode, final boolean fin,
			final boolean masked, final byte[] data, final int offset,
			final int length, EncodedFrame reuse) throws EncodeException {
		// Create a new frame if the user passes us null
		if(reuse == null) reuse = new EncodedFrame();
		reuse.valid = false;
		
		int header = 0;
		
		// TODO: Check opcode for correctness. For now we'll just
		// bitwise AND it to make sure it fits.
		header |= opcode & OPCODE_MASK;
		
		if(fin) header |= FIN_BIT;
		else header &= ~FIN_BIT;
		
		if(masked) header |= MASK_BIT;
		else header &= ~MASK_BIT;
		
		int extraBytes = 0;
	
		if(length < 126) {
			header |= length & PAYLOAD_MASK;
		} else if(length < Short.MAX_VALUE) {
			header |= 126 & PAYLOAD_MASK;
			reuse.payloadExtra[0] = (byte)((length & 0xFF00) >>> 8);
			reuse.payloadExtra[1] = (byte)((length & 0x00FF) >>> 0);
			extraBytes += 2;
		} else {
			header |= 127 & PAYLOAD_MASK;
			reuse.payloadExtra[4] = (byte)((length & 0xFF000000) >>> 24);
			reuse.payloadExtra[5] = (byte)((length & 0x00FF0000) >>> 16);
			reuse.payloadExtra[6] = (byte)((length & 0x0000FF00) >>> 8);
			reuse.payloadExtra[7] = (byte)((length & 0x000000FF) >>> 0);
			extraBytes += 8;
		}
		
		byte[] mask = { 0, 0, 0, 0 };
		int payloadStart = 2 + extraBytes;
		if(masked) {
			for(int i = 0; i < mask.length; ++i) {
				mask[i] = (byte)(Math.random() * 256.0);
			}
			
			payloadStart += 4;
		}
		
		if(reuse.raw.length < payloadStart + length) {
			reuse.raw = new byte[payloadStart + length];
		}
		
		byte[] raw = reuse.raw;
		
		// Write out header
		raw[0] = (byte)((header & 0xFF00) >>> 8); 
		raw[1] = (byte)((header & 0x00FF) >>> 0);
		
		System.arraycopy(reuse.payloadExtra, 0, raw, 2, extraBytes);
		
		if(masked) {
			System.arraycopy(mask, 0, raw, 2 + extraBytes, mask.length);
		}
		
		for(int i = payloadStart, j = offset; j < length; ++j, ++i) {
			// mask is zeroed out in case MASK_BIT isn't set
			raw[i] = (byte)(data[j] ^ mask[j % 4]);
		}
		
		reuse.payloadLength = length;
		reuse.payloadStart = payloadStart;
		reuse.totalLength = payloadStart + length;

		reuse.valid = true;
		
		return reuse;
	}
}
