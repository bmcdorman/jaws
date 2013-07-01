package com.github.jaws.proto.v13;

import com.github.jaws.proto.Message;
import static com.github.jaws.proto.v13.HeaderConstants.*;

/**
 *
 * @author Braden McDorman
 */
public class MessageHelpers {
	public static int getOpcode(final Message.Type type) {
		switch(type) {
			case Text: return TEXT_FRAME_OPCODE;
			case Binary: return BINARY_FRAME_OPCODE;
			case Ping: return PING_FRAME_OPCODE;
			case Pong: return PONG_FRAME_OPCODE;
			default: return -1;
		}
	}
}
