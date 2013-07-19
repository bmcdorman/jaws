package com.github.jaws.transparency;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Braden McDorman
 */
public class TransparentServerWebSocket extends ServerSocket {
	public TransparentServerWebSocket() throws IOException {
		super();
	}

	public TransparentServerWebSocket(int i) throws IOException {
		super(i);
	}

	public TransparentServerWebSocket(int i, int i1) throws IOException {
		super(i, i1);
	}

	public TransparentServerWebSocket(int i, int i1, InetAddress ia) throws IOException {
		super(i, i1, ia);
	}
	
	@Override
	public Socket accept() throws IOException {
		final AbstractTransparentWebSocket s = new TransparentWebSocketServer();
		
		// See java.net.Socket documentation for info on
		// what's going on here
		implAccept(s);
		
		return s;
	}
}
