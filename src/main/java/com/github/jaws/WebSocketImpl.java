package com.github.jaws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;

public class WebSocketImpl extends SocketImpl {

	private enum State {
		AwaitingUpgradeHeader,
		Connected,
		Disconnected
	};
	
	private State currentState;
	private OutputStream outputStream = null;
	
	@Override
	public Object getOption(int optId) throws SocketException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOption(int optId, Object value) throws SocketException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void accept(SocketImpl arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int available() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void bind(InetAddress addr, int port) throws IOException {
		
	}

	@Override
	protected void close() throws IOException {
		
	}

	@Override
	protected void connect(String host, int port) throws IOException {
		
	}

	@Override
	protected void connect(InetAddress addr, int port) throws IOException {
		
	}

	@Override
	protected void connect(SocketAddress addr, int port) throws IOException {
		
	}

	@Override
	protected void create(boolean stream) throws IOException {
		if(!stream) throw new WebSocketException("WebSockets do not support UDP.");
		
	}

	@Override
	protected InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected OutputStream getOutputStream() throws IOException {
		// if(outputStream == null) outputStream = new EncodedOutputStream(super.getOutputStream());
		return null;
	}

	@Override
	protected void listen(int arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendUrgentData(int arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
	

}
