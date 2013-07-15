package com.github.jaws.proto;

import com.github.jaws.http.HttpRequestHeader;
import java.util.ArrayList;
import java.util.List;

// Methods
import static com.github.jaws.http.HttpHeader.*;

// Get Http Request keys
import static com.github.jaws.http.HttpRequestHeader.*;
import static com.github.jaws.proto.Handshake.SEC_WEBSOCKET_VERSION_KEY;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Braden McDorman
 */
public class ClientHandshake extends Handshake {
	private final List<String> protocols = new ArrayList<String>();
	private String resourceName = "/";
	
	public static final String UPGRADE_VALUE = "websocket";
	
	public void setProtocols(final List<String> protocols) {
		if(protocols == null) throw new NullPointerException("Protocols must be a non-null"
			+ " list");
		
		this.protocols.clear();
		this.protocols.addAll(protocols);
	}
	
	public boolean addProtocol(final String protocol) {
		if(protocol == null) throw new NullPointerException("Protocol can not be null");
		
		if(protocols.contains(protocol)) return false;
		protocols.add(protocol);
		return true;
	}
	
	public boolean removeProtocol(final String protocol) {
		if(protocol == null) throw new NullPointerException("Null protocols can not be"
			+ " added, and thusly can not be removed");
		
		return protocols.remove(protocol);
	}
	
	public List<String> getProtocols() {
		return protocols;
	}
	
	public void setResourceName(final String resourceName) {
		if(resourceName == null) throw new NullPointerException("Null resource names not"
			+ " allowed");
		if(resourceName.isEmpty()) throw new IllegalArgumentException("Resource name must"
			+ "be at least one character (/ if necessary)");
		
		this.resourceName = resourceName;
	}
	
	public String getResourceName() {
		return resourceName;
	}
	
	public static ClientHandshake parseClientHandshake(final HttpRequestHeader header)
			throws HandshakeException {
		if(header == null) throw new NullPointerException("Header can't be null");
		ClientHandshake ret = new ClientHandshake();
		
		if(!header.getMethod().equals(GET_METHOD)) {
			throw new HandshakeException("Method must be GET");
		}
		
		{
			final List<String> upgrades = header.getField(UPGRADE_KEY);
			if(upgrades.size() != 1) throw new HandshakeException("Must have one"
				+ " and only one upgrade value");
			final String upgrade = upgrades.get(0);
			if(!upgrade.equals(UPGRADE_VALUE)) {
				throw new HandshakeException("Incorrect upgrade value. Expected"
					+ " " + UPGRADE_VALUE + ", but got " + upgrade);
			}
		}
		
		{
			final List<String> connections = header.getField(CONNECTION_KEY);
			boolean foundUpgrade = false;
			for(String value : connections) {
				if(value.equals(UPGRADE_KEY)) {
					foundUpgrade = true;
					break;
				}
			}
			if(!foundUpgrade) {
				throw new HandshakeException("Connection did not contain Upgrade"
					+ " request");
			}
		}
		
		{
			final List<String> keyBase64s = header.getField(SEC_WEBSOCKET_KEY_KEY);
			if(keyBase64s == null) throw new HandshakeException("No websocket key");
			if(keyBase64s.size() != 1) throw new HandshakeException("One and only one"
				+ " websocket key must be specified.");
			final String keyBase64 = keyBase64s.get(0);
			final byte[] key = Base64.decodeBase64(keyBase64);
			if(key.length != KEY_LENGTH) {
				throw new HandshakeException("Key is not " + KEY_LENGTH + " bytes");
			}
			ret.setKey(key);
		}
		
		{
			List<String> versions = header.getField(SEC_WEBSOCKET_VERSION_KEY);
			if(versions == null) throw new HandshakeException("No version key");
			
			final String version = versions.get(0);
			for(final String v : versions) {
				ret.addVersion(Integer.parseInt(v));
			}
		}
		
		{
			final List<String> exts = header.getField(SEC_WEBSOCKET_EXTENSIONS_KEY);
			if(exts != null) ret.setExtensions(exts);
		}
		
		{
			final List<String> protos = header.getField(SEC_WEBSOCKET_PROTOCOL_KEY);
			if(protos != null) ret.setProtocols(protos);
		}
		
		ret.setResourceName(header.getRequestUri());
		
		return ret;
	}
	
	public HttpRequestHeader getHeader() {
		if(getKey() == null) throw new NullPointerException("Key must be set before calling"
			+ " getHeader");
		
		HttpRequestHeader header = new HttpRequestHeader();
		header.setRequestUri(resourceName);
		header.setMethod(GET_METHOD);
		header.setVersion(HTTP_VERSION);
		
		header.addField(UPGRADE_KEY, UPGRADE_VALUE);
		header.addField(CONNECTION_KEY, CONNECTION_VALUE);
		
		{
			for(Integer i : getVersions()) {
				header.addField(SEC_WEBSOCKET_VERSION_KEY, Integer.toString(i));
			}
		}
		
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
			String protos = "";
			boolean firstProto = true;
			for(String proto : protocols) {
				if(!firstProto) protos += ", ";
				else firstProto = false;

				protos += proto;
			}
			if(!protos.isEmpty()) header.addField(SEC_WEBSOCKET_PROTOCOL_KEY, protos);
		}
		
		header.addField(SEC_WEBSOCKET_KEY_KEY, new String(Base64.encodeBase64(getKey())));
		
		return header;
	}
}
