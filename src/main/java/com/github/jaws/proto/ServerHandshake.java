/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.proto;

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
	
	public enum Type {
		Success,
		Failure
	};
	
	private Type type = Type.Success;
	
	public void setType(final Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setVersions(final List<Integer> versions) {
		
	}
	
	public void setProtocol(final String protocol) {
		this.protocol = protocol;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public static ServerHandshake parseServerHandshake(final HttpResponseHeader res)
			throws HandshakeException {
		if(res == null) throw new NullPointerException("");
		
		
		// FIXME: This isn't the DRYest
		if(!res.getVersion().equals("HTTP/1.1")) {
			throw new HandshakeException("Version must be HTTP/1.1");
		}
		
		final ServerHandshake ret = new ServerHandshake();
		
		// Now we need to detect if this is a failure type or success type
		// handshake
		final HttpResponseHeader.StatusCodeType type = res.getStatusCodeType();
		if(type == HttpResponseHeader.StatusCodeType.Informational) {
			ret.type = Type.Success;
			
			if(!res.getReasonPhrase().equals("Switching Protocols")) {
				throw new HandshakeException("Reason wasn't"
					+ " \"Switching Protocols\" but status code was"
					+ " 1xx");
			}
		} else if(type == HttpResponseHeader.StatusCodeType.ClientError) {
			ret.type = Type.Failure;
		} else {
			throw new HandshakeException("Unknown status code type "
				+ res.getStatusCode());
		}
		
		// Failure type handshakes will have a protocol key attached
		if(ret.type == Type.Success) {
			final List<String> protos = res.getField(SEC_WEBSOCKET_PROTOCOL_KEY);
			if(protos.size() > 1) {
				throw new HandshakeException("Expecting no more than one protocol"
					+ " specification.");
			}
		
			if(protos.size() == 1) ret.setProtocol(protos.get(0));
		}
		
		// Failure type handshakes will have version number(s) attached
		if(ret.type == Type.Failure) {
			List<String> versions = res.getField(SEC_WEBSOCKET_VERSION_KEY);
			if(versions == null) throw new HandshakeException("No version key");
			
			for(final String v : versions) {
				ret.addVersion(Integer.parseInt(v));
			}
		}
		
		return ret;
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

		{
			for(Integer i : getVersions()) {
				header.addField(SEC_WEBSOCKET_VERSION_KEY, Integer.toString(i));
			}
		}
		
		if(protocol != null) header.addField(SEC_WEBSOCKET_PROTOCOL_KEY, protocol);
		
		return header;
	}
}
