package com.github.jaws.proto;

/**
 * Encapsulates a single, complete WebSocket message.
 * 
 * @author Braden McDorman
 */
public class Message {
	public enum Type {
		Invalid,
		Text,
		Binary,
		Ping,
		Pong,
		CloseConnection
	}
	
	private Type type = Type.Invalid;
	private byte[] data;
	private int dataLength;
	
	/**
	 * @return The opcode type of this message
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return The data associated with this message. Encoding is type-dependent. 
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Used to retrieve the logical size of the data associated with this message. The
	 * data's length member should not be used to determine the size of the message's data.
	 * 
	 * @return The logical size of this message's data
	 */
	public int getDataLength() {
		return dataLength;
	}
	
	/**
	 * Associate a type or opcode with this message.
	 * 
	 * @param type The type of this message
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	public void setData(final byte[] data) {
		if(data == null) throw new NullPointerException("Data can't be null");
		this.data = data;
		this.dataLength = data.length;
	}
	
	public void setData(final byte[] data, final int length) {
		if(data == null) throw new NullPointerException("Data can't be null");
		this.data = data;
		this.dataLength = length;
	}
	
	private void ensureDataCapacity(final int length) {
		if(length <= data.length) return;
		data = new byte[length];
		dataLength = length;
	}
	
	public void copyData(final byte[] data) {
		ensureDataCapacity(data.length);
		System.arraycopy(data, 0, this.data, 0, data.length);
	}
}
