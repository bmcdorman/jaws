package com.github.jaws.util;

import java.util.ArrayList;
import java.util.List;

public class VersionList {
	public static List<Integer> intersection(final List<Integer> a, final List<Integer> b) {
		List<Integer> ret = new ArrayList<Integer>();
		
		// This isn't very efficient, but we're more than likely dealing with n < 5.
		// Sorting and doing it correctly (with a modified merge operation) probably
		// isn't worth it.
		
		for(final Integer v : a) {
			if(ret.contains(v)) continue;
			if(b.contains(v)) ret.add(v); 
		}
		
		for(final Integer v : b) {
			if(ret.contains(v)) continue;
			if(a.contains(v)) ret.add(v); 
		}
		
		return ret;
	}
}
