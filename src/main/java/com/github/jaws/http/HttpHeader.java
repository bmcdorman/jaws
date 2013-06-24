/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Braden McDorman
 */
public abstract class HttpHeader {
	public final static String HTTP_VERSION = "HTTP/1.1";
	
	public final static String OPTIONS_METHOD = "OPTIONS";
	public final static String GET_METHOD = "GET";
	public final static String HEAD_METHOD = "HEAD";
	public final static String POST_METHOD = "POST";
	public final static String PUT_METHOD = "PUT";
	public final static String DELETE_METHOD = "DELETE";
	public final static String TRACE_METHOD = "TRACE";
	public final static String CONNECT_METHOD = "CONNECT";
	
	public final static String SEPARATOR = " ";
	public final static String LINE_BREAK = "\r\n";
	public final static String HEADER_END = LINE_BREAK + LINE_BREAK;
	public final static char KEY_SUFFIX = ':';
	
	private Map<String, List<String>> fields = new HashMap<String, List<String>>();
	private String version;
	
	private void initalizeField(final String key) {
		if(fields.containsKey(key)) return;
		
		fields.put(key, new ArrayList<String>());
	}
	
	public void addField(final String key, final String value) {
		if(key == null || value == null) throw new NullPointerException();
		initalizeField(key);
		fields.get(key).add(value);
	}
	
	public void setField(final String key, final List<String> values) {
		initalizeField(key);
		final List<String> v = fields.get(key);
		v.clear();
		v.addAll(values);
	}
	
	public boolean removeField(final String key) {
		if(!fields.containsKey(key)) return false;
		return fields.remove(key) != null;
	}
	
	public List<String> getField(final String key) {
		return fields.get(key);
	}
	
	public Set<String> getFields() {
		return fields.keySet();
	}
	
	public void setVersion(final String version) {
		if(version == null) throw new NullPointerException();
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	protected abstract String getFirstLine();
	
	public String generateString() {
		if(version == null) {
			throw new NullPointerException("Version must be"
				+ "set before constructing a header.");
		}
		String ret = getFirstLine() + LINE_BREAK;
		for(String key : fields.keySet()) {
			ret += key + String.valueOf(KEY_SUFFIX) + SEPARATOR;
			final List<String> values = getField(key);
			boolean first = true;
			for(String value : values) {
				if(first) first = false;
				else ret += ", ";
				ret += value;
			}
			ret += LINE_BREAK;
		}
		ret += LINE_BREAK;
		return ret;
	}
	
	protected boolean parseFields(final BufferedReader fields) throws IOException {
		List<String> values = new ArrayList<String>();
		for(;;) {
			final String line = fields.readLine();
			if(line == null || line.isEmpty()) break;
			
			int keyEnd = line.indexOf(SEPARATOR);
			final int valueBegin = keyEnd + 1;
			// Malformed key-value pair
			if(keyEnd <= 0) continue;
			// Get rid of KEY_SUFFIX
			--keyEnd;
						
			final String key = line.substring(0, keyEnd);
			final String value = line.substring(valueBegin);
			
			values.clear();
			for(String v : value.split(",")) {
				values.add(v.trim());
			}
			
			setField(key, values);
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		String ret = "";
		for(String field : getFields()) {
			List<String> values = getField(field);
			boolean first = true;
			ret += field + ": ";
			for(String value : values) {
				if(first) first = false;
				else ret += ", ";
				ret += value;
			}
			ret += "\n";
		}
		return ret;
	}
}
