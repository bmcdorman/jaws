package com.github.jaws.proto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

public class ProtocolFactoryTest {
	private class MyProtocolFactory extends ProtocolFactory {
		private List<Integer> versions;
		
		public void setSupportedVersions(final List<Integer> versions) {
			this.versions = versions;
		}
		
		@Override
		public List<Integer> getSupportedVersions() {
			return versions;
		}

		@Override
		public IncomingStreamProcessor createIncomingStreamProcessor(
				int version, Role role) {
			return null;
		}

		@Override
		public OutgoingStreamProcessor createOutgoingStreamProcessor(
				int version, Role role) {
			return null;
		}
	}
	
	@Test
	public void latestSupportedVersion() {
		MyProtocolFactory factory = new MyProtocolFactory();
		
		List<Integer> versions = new ArrayList<Integer>();
		versions.add(11);
		versions.add(13);
		
		factory.setSupportedVersions(versions);
		
		List<Integer> actual = new ArrayList<Integer>();
		actual.add(11);
		assertEquals("Latest version mismatch", 11, factory.getLatestSupported(actual));
	}
}
