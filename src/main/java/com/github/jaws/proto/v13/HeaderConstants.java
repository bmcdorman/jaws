package com.github.jaws.proto.v13;

public class HeaderConstants {
	// See pg. 28 of RFC 6455
	public static final int FIN_BIT = 0x8000;
	
	public static final int RSV1_BIT = 0x4000;
	public static final int RSV2_BIT = 0x2000;
	public static final int RSV3_BIT = 0x1000;
	
	public static final int MASK_BIT = 0x0080;
	public static final int OPCODE_MASK = 0x0F00;
	public static final int PAYLOAD_MASK = 0x007F;
	
	// See pg. 66 of RFC 6455
	public static final int CONTINUATION_FRAME_OPCODE = 0x0000;
	public static final int TEXT_FRAME_OPCODE = 0x0100;
	public static final int BINARY_FRAME_OPCODE = 0x0200;
	public static final int CONNECTION_CLOSE_FRAME_OPCODE = 0x0800;
	public static final int PING_FRAME_OPCODE = 0x0900;
	public static final int PONG_FRAME_OPCODE = 0x0A00;
}
