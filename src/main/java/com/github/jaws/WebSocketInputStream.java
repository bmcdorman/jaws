package com.github.jaws;

import java.io.InputStream;

/**
 *
 * @author Braden McDorman
 */
public class WebSocketInputStream extends InputStream {
	@Override
	public int read() {
		return -1;
	}
}
