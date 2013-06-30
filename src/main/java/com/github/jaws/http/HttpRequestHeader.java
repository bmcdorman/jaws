/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.http;

import com.github.jaws.http.HttpHeader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Braden McDorman
 */
public class HttpRequestHeader extends HttpHeader {
	// Generated with some javascript using the HTTP field table from
	// https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
	public static final String ACCEPT_KEY = "Accept";
	public static final String ACCEPT_CHARSET_KEY = "Accept-Charset";
	public static final String ACCEPT_DATETIME_KEY = "Accept-Datetime";
	public static final String ACCEPT_ENCODING_KEY = "Accept-Encoding";
	public static final String ACCEPT_LANGUAGE_KEY = "Accept-Language";
	public static final String AUTHORIZATION_KEY = "Authorization";
	public static final String CACHE_CONTROL_KEY = "Cache-Control";
	public static final String CONNECTION_KEY = "Connection";
	public static final String CONTENT_LENGTH_KEY = "Content-Length";
	public static final String CONTENT_MD5_KEY = "Content-MD5";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String COOKIE_KEY = "Cookie";
	public static final String DATE_KEY = "Date";
	public static final String EXPECT_KEY = "Expect";
	public static final String FROM_KEY = "From";
	public static final String HOST_KEY = "Host";
	public static final String IF_MATCH_KEY = "If-Match";
	public static final String IF_MODIFIED_SINCE_KEY = "If-Modified-Since";
	public static final String IF_NONE_MATCH_KEY = "If-None-Match";
	public static final String IF_RANGE_KEY = "If-Range";
	public static final String IF_UNMODIFIED_SINCE_KEY = "If-Unmodified-Since";
	public static final String MAX_FORWARDS_KEY = "Max-Forwards";
	public static final String ORIGIN_KEY = "Origin";
	public static final String PRAGMA_KEY = "Pragma";
	public static final String PROXY_AUTHORIZATION_KEY = "Proxy-Authorization";
	public static final String RANGE_KEY = "Range";
	public static final String REFERER_KEY = "Referer";
	public static final String TE_KEY = "TE";
	public static final String UPGRADE_KEY = "Upgrade";
	public static final String USER_AGENT_KEY = "User-Agent";
	public static final String VIA_KEY = "Via";
	public static final String WARNING_KEY = "Warning";
	public static final String X_REQUESTED_WITH_KEY = "X-Requested-With";
	public static final String DNT_KEY = "DNT";
	public static final String X_FORWARDED_FOR_KEY = "X-Forwarded-For";
	public static final String X_FORWARDED_PROTO_KEY = "X-Forwarded-Proto";
	public static final String FRONT_END_HTTPS_KEY = "Front-End-Https";
	public static final String X_ATT_DEVICEID_KEY = "X-ATT-DeviceId";
	public static final String X_WAP_PROFILE_KEY = "X-Wap-Profile";
	public static final String PROXY_CONNECTION_KEY = "Proxy-Connection";
	
	private String method;
	private String requestUri;
	
	/**
	 * 
	 * @param method 
	 */
	public void setMethod(final String method) {
		if(method == null) throw new NullPointerException();
		this.method = method;
	}
	
	public String getMethod() {
		return method;
	}
	
	/**
	 * The entire request URI 
	 * 
	 * @param requestUri 
	 */
	public void setRequestUri(final String requestUri) {
		if(requestUri == null) throw new NullPointerException();
		this.requestUri = requestUri;
	}
	
	/**
	 * @return The previously set request uri for this request header
	 */
	public String getRequestUri() {
		return requestUri;
	}
	
	@Override
	protected String getFirstLine() {
		if(method == null || requestUri == null) {
			throw new NullPointerException("Method and Request URI must be"
				+ "set before constructing a header.");
		}
		return method + SEPARATOR + requestUri + SEPARATOR + getVersion();
	}
	
	public static HttpRequestHeader parseRequestHeader(final InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String firstLine = reader.readLine();
		if(firstLine == null || firstLine.isEmpty()) return null;
		final String[] firstLineParts = firstLine.split(SEPARATOR);
		if(firstLineParts.length != 3) return null;
		HttpRequestHeader ret = new HttpRequestHeader();
		ret.setMethod(firstLineParts[0]);
		ret.setRequestUri(firstLineParts[1]);
		ret.setVersion(firstLineParts[2]);
		ret.parseFields(reader);
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = method + " " + requestUri + " " + getVersion() + "\n";
		ret += super.toString();
		return ret;
	}
}
