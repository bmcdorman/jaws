package com.github.jaws;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

public class WebSocketImplFactory implements SocketImplFactory {
	@Override
	public SocketImpl createSocketImpl() {
		return new WebSocketImpl();
	}
}
