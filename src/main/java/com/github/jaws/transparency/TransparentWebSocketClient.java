package com.github.jaws.transparency;

import com.github.jaws.ClientWebSocket;
import com.github.jaws.WebSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;

/**
 * TransparentWebSocketServer (as opposed to TransparentServerWebSocket) is the Socket
 * implementation that the TransparentServerWebSocket returns when accept()ing
 * connections from peers. This naming is awful. Sorry.
 * 
 * @author Braden McDorman
 */
public class TransparentWebSocketClient extends AbstractTransparentWebSocket {	
	protected WebSocket createBacking(final InputStream in, final OutputStream out)
			throws IOException {
		return new ClientWebSocket(in, out);
	}
	
	public TransparentWebSocketClient() {
	}

	public TransparentWebSocketClient(final Proxy proxy) {
		super(proxy);
	}

	public TransparentWebSocketClient(String string, int i) throws UnknownHostException,
			IOException {
		super(string, i);
	}

	public TransparentWebSocketClient(InetAddress ia, int i) throws IOException {
		super(ia, i);
	}

	public TransparentWebSocketClient(final String string, int i, InetAddress ia, int i1)
			throws IOException {
		super(string, i, ia, i1);
	}

	public TransparentWebSocketClient(final InetAddress ia, final int i, InetAddress ia1,
			int i1) throws IOException {
		super(ia, i, ia1, i1);
	}
}
