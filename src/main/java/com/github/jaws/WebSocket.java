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
	
	/**
	 * Constructs a new WebSocket with a custom protocol factory.
	 * 
	 * @param socket The TCP socket to associate with this WebSocket
	 * @param  factory The custom protocol factory used to fetch the WebSocket backend
	 * @throws IOException If accessing the socket's IO streams results in an IO Exception
	 */
	public WebSocket(final Socket socket, final ProtocolFactory factory) throws IOException {
		this(socket.getInputStream(), socket.getOutputStream(), factory);
	}
	
	/**
	 * Constructs a new WebSocket with the default protocol factory.
	 * 
	 * @param socket The TCP socket to associate with this WebSocket
	 * @throws IOException If accessing the socket's IO streams results in an IO Exception
	 */
	public WebSocket(final Socket socket) throws IOException {
		this(socket, new DefaultProtocolFactory());
	}
	
	/**
	 * WebSockets begin life as regular HTTP connections and are later "upgraded" to the
	 * WebSocket protocol. getMode() can be used to determine which mode the WebSocket
	 * is currently operating in.
	 * 
	 * Certain functions, like send() and recv(), will not work until the connection
	 * is upgraded to WebSocket mode.
	 * 
	 * @return The mode the WebSocket is currently operating in.
	 */
	public Mode getMode() {
		return mode;
	}
	
	/**
	 * Queue a message to be sent to the connected peer.
	 * 
	 * @see poll()
	 * @param message The message to send to the connected peer.
	 * @throws WebSocketException If the HTTP connection has not yet been upgraded to WebSocket
	 */
	public void send(final Message message) throws WebSocketException {
		if(mode != Mode.WebSocket) {
			throw new WebSocketException("Can't send messages until handshake is"
				+ " established");
		}
		outp.admit(message);
	}
	
	/**
	 * recv() is used to receive the next queued WebSocket message.
	 * 
	 * @see poll()
	 * @return The next queued message or null if no messages are available.
	 * @throws WebSocketException If the HTTP connection has not yet been upgraded to WebSocket
	 */
	public Message recv() throws WebSocketException {
		if(mode != Mode.WebSocket) {
			throw new WebSocketException("Can't recv messages until handshake is"
				+ " established");
		}
		return inp.nextMessage();
	}
	
	/**
	 * @return The protocol factory that this object was constructed with.
	 */
	public ProtocolFactory getProtocolFactory() {
		return factory;
	}
	
	/**
	 * Send the closing WebSocket handshake to the connected peer.
	 * Communication with the peer after calling close() is undefined.
	 * 
	 * Please note that close() does NOT close the associated TCP socket.
	 * Once close() has been called, the socket must be manually closed as well.
	 * 
	 */
	public void close() throws WebSocketException, IOException {
		Message close = new Message();
		close.setType(Message.Type.CloseConnection);
		send(close);
		poll();
	}
	
	protected abstract void processIncomingData() throws WebSocketException, IOException;
	protected abstract void processOutgoingData() throws WebSocketException, IOException;
	
	/**
	 * Poll writes out the queued messages to the connected peer and reads in
	 * messages from the connected peer, placing them in an incoming message queue.
	 * 
	 * This should be called before recv() operation(s) and after send() operation(s).
	 * 
	 * @throws WebSocketException
	 * @throws IOException If there was an error reading from or writing to the associated
	 * I/O streams.
	 */
	public void poll() throws WebSocketException, IOException {
		processOutgoingData();
		processIncomingData();
	}
}
