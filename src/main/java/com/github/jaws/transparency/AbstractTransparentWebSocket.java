package com.github.jaws.transparency;

import com.github.jaws.ClientWebSocket;
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
 *
 * @author Braden McDorman
 */
public abstract class AbstractTransparentWebSocket extends Socket {
	private WebSocket backend = null;
	private PollingOutputStream outputStream = null;
	private PollingInputStream inputStream = null;
	
	private void ioPoll() throws IOException {
		try {
			backend.poll();
		} catch(final WebSocketException e) {
			throw new IOException(e.getCause());
		}
	}
	
	private class PollingInputStream extends InputStream {
		private InputStream backing;
		
		public PollingInputStream(final InputStream backing) {
			this.backing = backing;
		}
		
		@Override
		public int read() throws IOException {
			ioPoll();
			return backing.read();
		}
		
		@Override
		public int read(byte[] b) throws IOException {
			ioPoll();
			return backing.read(b);
		}
		
		@Override
		public int read(byte[] b, int offset, int length) throws IOException {
			ioPoll();
			return backing.read(b, offset, length);
		}
	}
	
	private class PollingOutputStream extends OutputStream {
		private OutputStream backing;
	
		public PollingOutputStream(final OutputStream backing) {
			this.backing = backing;
		}
		
		@Override
		public void write(int b) throws IOException {
			backing.write(b);
			ioPoll();
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			backing.write(b);
			ioPoll();
		}
		
		@Override
		public void write(byte[] b, int offset, int length) throws IOException {
			backing.write(b, offset, length);
			ioPoll();
		}
	}
	
	private void lazyInit() throws IOException {
		if(backend != null) return;
		
		try {
			outputStream = new PollingOutputStream(getOutputStream());
			inputStream = new PollingInputStream(getInputStream());
			backend = createBacking(inputStream, outputStream);
		} catch(final IOException e) {
			outputStream = null;
			inputStream = null;
			backend = null;
			throw e;
		}
	}
	
	/**
	 * Subclasses implement this method to return the appropriate
	 * WebSocket backing for the transparent socket to use. Since
	 * the server WebSocket messages and client WebSocket
	 * messages differ, this pattern or another one is essential.
	 * 
	 * @param in The InputStream to associate with the backing
	 * @param out The OutputStream to associate with the backing
	 * @return The WebSocket backing that this socket should use
	 */
	protected abstract WebSocket createBacking(final InputStream in, final OutputStream out)
		throws IOException;
	
	/**
	 * Construct a new Transparent WebSocket with the default client WebSocket
	 * backend.
	 */
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
}
