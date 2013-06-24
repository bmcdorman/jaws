/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.proto;

import com.github.jaws.http.HttpRequestHeader;
import java.util.ArrayList;
import java.util.List;

// Methods
import static com.github.jaws.http.HttpHeader.*;

// Get Http Request keys
import static com.github.jaws.http.HttpRequestHeader.*;

import com.github.jaws.http.HttpResponseHeader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Braden McDorman
 */
public class ServerHandshake extends Handshake {
	private String protocol;
	
	public static final String UPGRADE_VALUE = "WebSocket";
	
	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public HttpResponseHeader getHeader() {
		if(getKey() == null) throw new NullPointerException("Key must be set before calling"
			+ " getHeader");
		
		HttpResponseHeader header = new HttpResponseHeader();
		header.setVersion(HTTP_VERSION);
		header.setStatusCode(101);
		header.setReasonPhrase("Switiching Protocols");
		header.addDateField();
		header.addServerField();
		
		final byte[] accept = (new String(Base64.encodeBase64(getKey()))
			+ ACCEPT_MAGIC).getBytes();
		final byte[] sha = DigestUtils.sha1(accept);
		
		header.addField(UPGRADE_KEY, UPGRADE_VALUE);
		header.addField(CONNECTION_KEY, CONNECTION_VALUE);
		header.addField(SEC_WEBSOCKET_ACCEPT_KEY, new String(Base64.encodeBase64(sha)));
		
		{
			String exts = "";
			boolean firstExt = true;
			for(String ext : getExtensions()) {
				if(!firstExt) exts += ", ";
				else firstExt = false;

				exts += ext;
			}
			if(!exts.isEmpty()) header.addField(SEC_WEBSOCKET_EXTENSIONS_KEY, exts);
		}
		
		if(protocol != null) header.addField(SEC_WEBSOCKET_PROTOCOL_KEY, protocol);
		
		return header;
	}
}
