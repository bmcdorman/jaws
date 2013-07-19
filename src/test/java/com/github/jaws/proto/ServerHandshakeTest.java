package com.github.jaws.proto;

import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class ServerHandshakeTest  {
	@Test
	public void acceptKey() throws Exception {
		final String clientKey = "dGhlIHNhbXBsZSBub25jZQ==";
		final byte[] key = Base64.decodeBase64(clientKey);
		ServerHandshake hand = new ServerHandshake();
		hand.setKey(key);
		final List<String> aKeys = hand.getHeader()
			.getField(Handshake.SEC_WEBSOCKET_ACCEPT_KEY);
		assertEquals("One and only one accept key allowed", 1, aKeys.size());
		final String aKey = aKeys.get(0);
		assertEquals("Accept key mismatch", "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", aKey);
	}
}
