package com.github.jaws.proto.v13;

import com.github.jaws.proto.IncomingStreamProcessor;
import com.github.jaws.proto.Message;
import com.github.jaws.util.ResizableCircularByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.github.jaws.proto.v13.HeaderConstants.*;

/**
 * Implements a simple finite state machine and buffering system for processing
 * incoming frames.
 * 
 * @author Braden McDorman
 */
public class IncomingStreamProcessorV13 extends IncomingStreamProcessor {
	private ResizableCircularByteBuffer buffer = new ResizableCircularByteBuffer(1024);
	private DecodedFrame frame;
	private Queue<Message> availableMessages = new ConcurrentLinkedQueue<Message>();
	
	private Message.Type messageType = Message.Type.Invalid;
	private ResizableCircularByteBuffer messageDataBuffer
		= new ResizableCircularByteBuffer(1024);
	
	private enum State {
		Waiting,
		ReceivingMessage
	}
	
	private State state;
	
	@Override
	public void admit(final byte[] data) {
		buffer.write(data);
		if(!DecodedFrame.isSufficientData(buffer)) return;
		
		try {
			frame = DecodedFrame.decode(buffer, frame);
		} catch(DecodeException e) {
			System.err.println("FIXME: This is really bad. Our buffer is in an"
				+ " undefined state. Who knows where the next frame is now."
				+ " With that said, happily continuing in an undefined state.");
		}
		
		if(frame == null) {
			// TODO: Error
		}
		
		if(!frame.valid) {
			// TODO: Error
		}
		
		handleFrame();
	}
	
	private void handleFrame() {
		messageDataBuffer.write(frame.data, 0, frame.payloadLength);
		
		if(state == State.Waiting) {
			freshFrame();
		} else if(state == State.ReceivingMessage) {
			continuationFrame();
		}
	}
	
	private void freshFrame() {
		final int op = frame.header;
		if((op & CONTINUATION_FRAME_OPCODE) != 0) {
			System.err.println("FIXME: We were NOT expecting a continuation frame."
				+ " Bailing.");
			bail();
			return;
		}
		
		// This frame has the opcode in it, so store it
		// for later.
		if((op & TEXT_FRAME_OPCODE) != 0) {
			messageType = Message.Type.Text;
		} else if((op & BINARY_FRAME_OPCODE) != 0) {
			messageType = Message.Type.Binary;
		} else if((op & PING_FRAME_OPCODE) != 0) {
			messageType = Message.Type.Ping;
		} else if((op & PONG_FRAME_OPCODE) != 0) {
			messageType = Message.Type.Pong;
		}
		
		if((frame.header & FIN_BIT) == 0) state = State.ReceivingMessage;
		else publishMessage();
	}
	
	private void continuationFrame() {
		if((frame.header & CONTINUATION_FRAME_OPCODE) == 0) {
			System.err.println("FIXME: We were expecting a continuation frame."
				+ " Bailing.");
			bail();
			return;
		}
		
		// We're done!
		if((frame.header & FIN_BIT) != 0) {
			publishMessage();
			state = State.Waiting;
		}
	}
	
	private void bail() {
		state = State.Waiting;
		messageDataBuffer.clear();
	}
	
	private void publishMessage() {
		Message m = new Message();
		
		final byte[] data = new byte[messageDataBuffer.available()];
		m.setData(data);
		
		m.setType(messageType);
		
		availableMessages.add(m);
		
		messageDataBuffer.clear();
	}
	
	@Override
	public int getNumMessagesAvailable() {
		return availableMessages.size();
	}
	
	@Override
	public Message nextMessage() {
		return availableMessages.poll();
	}
}