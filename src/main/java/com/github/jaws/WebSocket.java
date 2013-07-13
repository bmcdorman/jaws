package com.github.jaws;

import com.github.jaws.http.HttpHeader;
import com.github.jaws.http.HttpRequestHeader;
import com.github.jaws.proto.ClientHandshake;
import com.github.jaws.proto.DefaultProtocolFactory;
import com.github.jaws.proto.HandshakeException;
import com.github.jaws.proto.IncomingStreamProcessor;
import com.github.jaws.proto.Message;
import com.github.jaws.proto.OutgoingStreamProcessor;
import com.github.jaws.proto.ProtocolFactory;
import com.github.jaws.proto.ServerHandshake;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * WebSocket is a decorator for an input stream and output stream that handles handshakes and
 * message passing. To use this class, establish a accept() an incoming socket connection
 * and construct a WebSocket from said socket connection.
 * 
 * @author Braden McDorman
 */
public abstract class WebSocket {
	public enum Mode {
		Http,
		WebSocket
	}
	
	protected InputStream in;
	protected OutputStream out;
	protected Mode mode = Mode.Http;
	
	protected ProtocolFactory factory;
	protected IncomingStreamProcessor inp;
	protected OutgoingStreamProcessor outp;
	
	public WebSocket(final InputStream in, final OutputStream out,
			final ProtocolFactory factory) {
		this.in = in;
		this.out = out;
		this.factory = factory;
	}
	
	public WebSocket(final InputStream in, final OutputStream out) {
		this(in, out, new DefaultProtocolFactory());
	}
	
	public WebSocket(final Socket socket, final ProtocolFactory factory) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream(), factory);
	}
	
	public WebSocket(final Socket socket) throws IOException {
		this(socket, new DefaultProtocolFactory());
	}
	
	public Mode getMode() {
		return mode;
	}
	
	public void send(final Message message) throws WebSocketException {
		if(mode != Mode.WebSocket) {
			throw new WebSocketException("Can't send messages until handshake is"
				+ " established");
		}
		outp.admit(message);
	}
	
	public Message recv() throws WebSocketException {
		if(mode != Mode.WebSocket) {
			throw new WebSocketException("Can't recv messages until handshake is"
				+ " established");
		}
		return inp.nextMessage();
	}
	
	public ProtocolFactory getProtocolFactory() {
		return factory;
	}
	
	public void close() throws WebSocketException, IOException {
		Message close = new Message();
		close.setType(Message.Type.CloseConnection);
		send(close);
		poll();
	}
	
	protected abstract void processIncomingData() throws WebSocketException, IOException;
	protected abstract void processOutgoingData() throws WebSocketException, IOException;
	
	public void poll() throws WebSocketException, IOException {
		processOutgoingData();
		processIncomingData();
	}
}
