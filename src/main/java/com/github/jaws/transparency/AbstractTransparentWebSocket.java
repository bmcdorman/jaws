package com.github.jaws.transparency;

import com.github.jaws.WebSocket;
import com.github.jaws.WebSocketException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class serves as the abstract base for the server's transparent
 * WebSocket and the client's transparent WebSocket. Almost all of the
 * logic (except the associated backend) is shared between these two
 * subclasses.
 * 
 * @author Braden McDorman
 */
public abstract class AbstractTransparentWebSocket extends Socket {
	private WebSocket backend = null;
	private WebSocketOutputStream outputStream = null;
	private WebSocketInputStream inputStream = null;
	
	/**
	 * Lazily initialize the polling io streams and backend. We have to
	 * do this lazily because the socket's io streams aren't valid until
	 * it has successfully connected.
	 * 
	 * @throws IOException If there was an error retrieving the socket's
	 * io streams or if there was an error creating the WebSocket backend.
	 */
	private void lazyInit() throws IOException {
		if(backend != null) return;
		
		try {
			backend = createBacking();
			outputStream = new WebSocketOutputStream(backend);
			inputStream = new WebSocketInputStream(backend);
		} catch(final IOException e) {
			outputStream = null;
			inputStream = null;
			backend = null;
			throw e;
		}
		
		outputStream.setAutoPolling(true);
		inputStream.setAutoPolling(true);
	}
	
	/**
	 * Subclasses implement this method to return the appropriate
	 * WebSocket backing for the transparent socket to use. Since
	 * the server WebSocket messages and client WebSocket
	 * messages differ, this pattern or another one is essential.
	 * 
	 * @return The WebSocket backing that this socket should use
	 */
	protected abstract WebSocket createBacking() throws IOException;
	
	public AbstractTransparentWebSocket() {
	}

	public AbstractTransparentWebSocket(final Proxy proxy) {
		super(proxy);
	}

	public AbstractTransparentWebSocket(String string, int i)
			throws UnknownHostException, IOException {
		super(string, i);
	}

	public AbstractTransparentWebSocket(InetAddress ia, int i) throws IOException {
		super(ia, i);
	}

	public AbstractTransparentWebSocket(final String string, int i,
			InetAddress ia, int i1) throws IOException {
		super(string, i, ia, i1);
	}

	public AbstractTransparentWebSocket(final InetAddress ia, final int i,
			InetAddress ia1, int i1) throws IOException {
		super(ia, i, ia1, i1);
	}
	
	protected InputStream getSocketInputStream() throws IOException {
		return super.getInputStream();
	}
	
	protected OutputStream getSocketOutputStream() throws IOException {
		return super.getOutputStream();
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		// This will fail if the stream isn't ready yet
		super.getInputStream();
		
		lazyInit();
		return inputStream;
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		// This will fail if the stream isn't ready yet
		super.getOutputStream();
		
		lazyInit();
		return outputStream;
	}
	
	/**
	 * Send the WebSocket closing handshake and close the associated socket.
	 * @throws IOException 
	 */
	@Override
	public void close() throws IOException {
		try {
			backend.close();
		} catch(final WebSocketException e) {
			throw new IOException(e.getCause());
		}
		super.close();
	}
}
