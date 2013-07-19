package com.github.jaws;

import static org.junit.Assert.*;

import com.github.jaws.http.HttpHeader;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 *
 * @author Braden McDorman
 */
@RunWith(JUnit4.class)
public class HttpHeaderTest {
	class DummyHeader extends HttpHeader {
		@Override
		protected String getFirstLine() {
			return "";
		} 
	}
	
	@Test
	public void httpVersionRetained() {
		HttpHeader header = new DummyHeader();
		
		final String ver = "HTTP/1.1";
		header.setVersion(ver);
		assertEquals("Version retained", ver, header.getVersion());
	}
	
	@Test(expected = NullPointerException.class)
	public void httpVersionNullPtr() {
		HttpHeader header = new DummyHeader();
		header.setVersion(null);
	}
	
	@Test
	public void emptyFieldsOnConstruct() {
		HttpHeader header = new DummyHeader();
		assertEquals("Fields present on construction", 0,
			header.getFields().size());
	}
	
	@Test(expected = NullPointerException.class)
	public void addFieldKeyNullPtr() {
		HttpHeader header = new DummyHeader();
		header.addField(null, "value");
	}
	
	@Test(expected = NullPointerException.class)
	public void addFieldValueNullPtr() {
		HttpHeader header = new DummyHeader();
		header.addField("key", null);
	}
	
	@Test(expected = NullPointerException.class)
	public void setFieldKeyNullPtr() {
		HttpHeader header = new DummyHeader();
		header.setField(null, new ArrayList<String>());
	}
	
	@Test(expected = NullPointerException.class)
	public void setFieldValueNullPtr() {
		HttpHeader header = new DummyHeader();
		header.setField("key", null);
	}
	
	@Test(expected = NullPointerException.class)
	public void getFieldKeyNullPtr() {
		HttpHeader header = new DummyHeader();
		header.getField(null);
	}
	
	@Test
	public void getNonExistantField() {
		HttpHeader header = new DummyHeader();
		assertEquals("Field is not null", null, header.getField("key"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setEmptyFieldKey() {
		HttpHeader header = new DummyHeader();
		header.setField("", new ArrayList<String>());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addEmptyFieldKey() {
		HttpHeader header = new DummyHeader();
		header.addField("", "value");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addEmptyValueKey() {
		HttpHeader header = new DummyHeader();
		header.addField("key", "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void getEmptyField() {
		HttpHeader header = new DummyHeader();
		header.getField("");
	}
	
	@Test(expected = NullPointerException.class)
	public void setFieldWithNullValue() {
		HttpHeader header = new DummyHeader();
		final List<String> values = new ArrayList<String>();
		values.add("first");
		values.add(null);
		
		header.setField("key", values);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setFieldWithEmptyValue() {
		HttpHeader header = new DummyHeader();
		final List<String> values = new ArrayList<String>();
		values.add("first");
		values.add("");
		
		header.setField("key", values);
	}
	
	@Test
	public void fieldManipulation() {
		HttpHeader header = new DummyHeader();
		final List<String> values = new ArrayList<String>();
		values.add("first");
		values.add("second");
		
		header.setField("key", values);
		
		assertEquals("Field not created", 1, header.getFields().size());
		assertEquals("Field doesn't have " + values.size() + " values",
			2, header.getField("key").size());
		
		assertEquals("Set field incorrect", values, header.getField("key"));
	}
	
	@Test(expected = NullPointerException.class)
	public void generateStringNoVersion() {
		HttpHeader header = new DummyHeader();
		header.generateString();
	}
}
