package com.github.jaws;

import com.github.jaws.http.HttpHeader;
import com.github.jaws.http.HttpResponseHeader;
import com.github.jaws.proto.ClientHandshake;
import com.github.jaws.proto.DefaultProtocolFactory;
import com.github.jaws.proto.HandshakeException;
import com.github.jaws.proto.ProtocolFactory;
import com.github.jaws.proto.ServerHandshake;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Braden McDorman
 */
public class ClientWebSocket extends WebSocket {
	// Prevent sending the request HTTP header multiple times
	// before we get a response. This probably should be changed.
	private boolean waitingOnResponse = false;
	
	public ClientWebSocket(final InputStream in, final OutputStream out,
			final ProtocolFactory factory) {
		super(in, out, factory);
	}
	
	public ClientWebSocket(final InputStream in, final OutputStream out) {
		super(in, out, new DefaultProtocolFactory());
	}
	
	public ClientWebSocket(final Socket socket, final ProtocolFactory factory) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream(), factory);
	}
	
	public ClientWebSocket(final Socket socket) throws IOException {
		super(socket, new DefaultProtocolFactory());
	}
	
	private void requestUpgradeConnection() throws WebSocketException, IOException {
		if(waitingOnResponse) return;
		final ClientHandshake c = new ClientHandshake();
		c.setVersions(factory.getSupportedVersions());
		final HttpHeader req = c.getHeader();
		final byte[] toSend = req.generateString().getBytes();
		out.write(toSend);
		waitingOnResponse = true;
	}
	
	@Override
	protected void processIncomingData() throws WebSocketException, IOException {
		if(in.available() <= 0) return;
		
		if(mode == WebSocket.Mode.Http) {
			final HttpResponseHeader res = HttpResponseHeader.parseResponseHeader(in);
			if(res == null) return;
			try {
				// Get the server's handshake
				ServerHandshake serverHandshake = ServerHandshake
					.parseServerHandshake(res);
				
				// Server said our connection failed
				if(serverHandshake.getType() != ServerHandshake.Type.Success) {
					throw new WebSocketException("Server rejected the"
						+ " connection (" + res.getReasonPhrase() + ")");
				}
				
				// WooHoo! Connected.
				mode = WebSocket.Mode.WebSocket;
				waitingOnResponse = false;
				
			} catch(final HandshakeException e) {
				throw new WebSocketException(e.getMessage());
			}
		} else if(mode == WebSocket.Mode.WebSocket) {
			inp.read(in);
		}
	}
	
	@Override
	protected void processOutgoingData() throws WebSocketException, IOException {
		if(mode == Mode.Http) {
			requestUpgradeConnection();
			return;
		}
		
		outp.write(out);
	}
}
