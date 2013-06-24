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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Braden McDorman
 */
public class HttpResponseHeader extends HttpHeader {
	// Generated with some javascript using the HTTP field table from
	// https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN_KEY = "Access-Control-Allow-Origin";
	public static final String ACCEPT_RANGES_KEY = "Accept-Ranges";
	public static final String AGE_KEY = "Age";
	public static final String ALLOW_KEY = "Allow";
	public static final String CACHE_CONTROL_KEY = "Cache-Control";
	public static final String CONNECTION_KEY = "Connection";
	public static final String CONTENT_ENCODING_KEY = "Content-Encoding";
	public static final String CONTENT_LANGUAGE_KEY = "Content-Language";
	public static final String CONTENT_LENGTH_KEY = "Content-Length";
	public static final String CONTENT_LOCATION_KEY = "Content-Location";
	public static final String CONTENT_MD5_KEY = "Content-MD5";
	public static final String CONTENT_DISPOSITION_KEY = "Content-Disposition";
	public static final String CONTENT_RANGE_KEY = "Content-Range";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String DATE_KEY = "Date";
	public static final String ETAG_KEY = "ETag";
	public static final String EXPIRES_KEY = "Expires";
	public static final String LAST_MODIFIED_KEY = "Last-Modified";
	public static final String LINK_KEY = "Link";
	public static final String LOCATION_KEY = "Location";
	public static final String P3P_KEY = "P3P";
	public static final String PRAGMA_KEY = "Pragma";
	public static final String PROXY_AUTHENTICATE_KEY = "Proxy-Authenticate";
	public static final String REFRESH_KEY = "Refresh";
	public static final String RETRY_AFTER_KEY = "Retry-After";
	public static final String SERVER_KEY = "Server";
	public static final String SET_COOKIE_KEY = "Set-Cookie";
	public static final String STATUS_KEY = "Status";
	public static final String STRICT_TRANSPORT_SECURITY_KEY = "Strict-Transport-Security";
	public static final String TRAILER_KEY = "Trailer";
	public static final String TRANSFER_ENCODING_KEY = "Transfer-Encoding";
	public static final String VARY_KEY = "Vary";
	public static final String VIA_KEY = "Via";
	public static final String WARNING_KEY = "Warning";
	public static final String WWW_AUTHENTICATE_KEY = "WWW-Authenticate";
	public static final String X_FRAME_OPTIONS_KEY = "X-Frame-Options";
	public static final String X_XSS_PROTECTION_KEY = "X-XSS-Protection";
	public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
	public static final String X_CONTENT_SECURITY_POLICY = "X-Content-Security-Policy";
	public static final String X_WEBKIT_CSP_KEY = "X-WebKit-CSP";
	public static final String X_CONTENT_TYPE_OPTIONS_KEY = "X-Content-Type-Options";
	public static final String X_POWERED_BY_KEY = "X-Powered-By";
	public static final String X_UA_COMPATIBLE_KEY = "X-UA-Compatible";
	
	private int statusCode;
	private String reasonPhrase;
	
	public void addServerField() {
		// TODO: This shouldn't be constant
		addField(SERVER_KEY, "jaws/1.0");
	}
	
	public void addDateField() {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		addField(DATE_KEY, format.format(now));
	}
	
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the reasonPhrase
	 */
	public String getReasonPhrase() {
		return reasonPhrase;
	}

	/**
	 * @param reasonPhrase the reasonPhrase to set
	 */
	public void setReasonPhrase(final String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}
	
	@Override
	protected String getFirstLine() {
		return getVersion() + SEPARATOR + Integer.toString(statusCode)
			+ SEPARATOR + reasonPhrase;
	}
	
	public static HttpResponseHeader parseResponseHeader(final InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		final String firstLine = reader.readLine();
		if(firstLine.isEmpty()) return null;
		final String[] firstLineParts = firstLine.split(SEPARATOR);
		if(firstLineParts.length != 3) return null;
		HttpResponseHeader ret = new HttpResponseHeader();
		ret.setVersion(firstLineParts[0]);
		ret.setStatusCode(Integer.parseInt(firstLineParts[1]));
		ret.setReasonPhrase(firstLineParts[2]);
		ret.parseFields(reader);
		return ret;
	}
	
	@Override
	public String toString() {
		String ret = getVersion() + " " + statusCode + " " + reasonPhrase + "\n";
		ret += super.toString();
		return ret;
	}
}
