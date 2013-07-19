package com.github.jaws;

import com.github.jaws.http.HttpHeader;
import com.github.jaws.http.HttpRequestHeader;
import com.github.jaws.proto.ClientHandshake;
import com.github.jaws.proto.DefaultProtocolFactory;
import com.github.jaws.proto.HandshakeException;
import com.github.jaws.proto.ProtocolFactory;
import com.github.jaws.proto.ServerHandshake;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Braden McDorman
 */
public class ServerWebSocket extends WebSocket {
	public ServerWebSocket(final InputStream in, final OutputStream out,
			final ProtocolFactory factory) {
		super(in, out, factory);
	}
	
	public ServerWebSocket(final InputStream in, final OutputStream out) {
		super(in, out, new DefaultProtocolFactory());
	}
	
	public ServerWebSocket(final Socket socket, final ProtocolFactory factory) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream(), factory);
	}
	
	public ServerWebSocket(final Socket socket) throws IOException {
		super(socket, new DefaultProtocolFactory());
	}
	
	@Override
	protected void processIncomingData() throws WebSocketException, IOException {
		if(in.available() <= 0) return;
		
		if(mode == WebSocket.Mode.Http) {
			final HttpRequestHeader req = HttpRequestHeader.parseRequestHeader(in);
			if(req == null) return;
			try {
				// Get the client's handshake
				ClientHandshake clientHandshake = ClientHandshake
					.parseClientHandshake(req);
				
				// Prepare our server handshake
				ServerHandshake serverHandshake = new ServerHandshake();
				serverHandshake.setKey(clientHandshake.getKey());
				final HttpHeader response = serverHandshake.getHeader();
				final byte[] toSend = response.generateString().getBytes();
				// System.out.println(response);
				
				// Write it out
				out.write(toSend);
				
				// Send our state machine to the WebSocket state
				mode = WebSocket.Mode.WebSocket;
				
				// System.out.println("Upgraded connection.");
				
				final List<Integer> versions = clientHandshake.getVersions();
				
				inp = factory.createIncomingStreamProcessor(versions,
					ProtocolFactory.Role.Server);
				outp = factory.createOutgoingStreamProcessor(versions,
					ProtocolFactory.Role.Server);
				// TODO: If inp or outp are null, we need to send back a fail
				// response instead of succcess
			} catch(HandshakeException e) {
				throw new WebSocketException(e.getMessage());
			}
		} else if(mode == WebSocket.Mode.WebSocket) {
			inp.read(in);
		}
	}
	
	@Override
	protected void processOutgoingData() throws WebSocketException, IOException {
		if(mode != Mode.WebSocket) return;
		outp.write(out);
	}
}
