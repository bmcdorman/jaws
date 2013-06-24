/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.proto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Braden McDorman
 */
public class Handshake {
	public static final String ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
	public static final String CONNECTION_VALUE = "Upgrade";
	public static final String SEC_WEBSOCKET_KEY_KEY = "Sec-WebSocket-Key";
	public static final String SEC_WEBSOCKET_VERSION_KEY = "Sec-WebSocket-Version";
	public static final String SEC_WEBSOCKET_PROTOCOL_KEY = "Sec-WebSocket-Protocol";
	public static final String SEC_WEBSOCKET_EXTENSIONS_KEY = "Sec-WebSocket-Extensions";
	public static final String SEC_WEBSOCKET_ACCEPT_KEY = "Sec-WebSocket-Accept";
	public static final int KEY_LENGTH = 16;
	
	private final List<String> extensions = new ArrayList<String>();
	private byte[] key;
	
	public void setExtensions(final List<String> protocols) {
		if(protocols == null) throw new NullPointerException("Extensions must be a non-null"
			+ " list");
		
		this.extensions.clear();
		this.extensions.addAll(protocols);
	}
	
	public boolean addExtension(final String extension) {
		if(extension == null) throw new NullPointerException("Extension can not be null");
		
		if(extensions.contains(extension)) return false;
		extensions.add(extension);
		return true;
	}
	
	public boolean removeExtension(final String extension) {
		if(extension == null) throw new NullPointerException("Null extensions can not be"
			+ " added, and thusly can not be removed");
		
		return extensions.remove(extension);
	}
	
	public List<String> getExtensions() {
		return extensions;
	}
	
	public void setKey(final byte[] key) {
		if(key == null) throw new NullPointerException();
		if(key.length != 16) throw new IllegalArgumentException("Keys must be"
			+ "exactly 16 bytes");
		
		this.key = key;
	}
	
	public byte[] getKey() {
		return key;
	}
}
