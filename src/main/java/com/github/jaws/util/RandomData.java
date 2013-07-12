/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.jaws.util;

/**
 *
 * @author Braden McDorman
 */
public class RandomData {
	public static byte[] getByteArray(int length) {
		final byte[] ret = new byte[length];
		for(int i = 0; i < length; ++i) {
			ret[i] = (byte)(Math.random() * 256);
		}
		return ret;
	}
}
