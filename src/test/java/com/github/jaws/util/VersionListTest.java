package com.github.jaws.util;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

public class VersionListTest {
	@Test
	public void emptyIntersection1() {
		final List<Integer> a = new ArrayList<Integer>();
		final List<Integer> b = new ArrayList<Integer>();
		
		b.add(213);
		
		assertEquals("Intersection with empty set should be"
				+ " empty", VersionList.intersection(a, b),
				new ArrayList<Integer>());
	}
	
	@Test
	public void emptyIntersection2() {
		final List<Integer> a = new ArrayList<Integer>();
		final List<Integer> b = new ArrayList<Integer>();
		
		a.add(213);
		
		assertEquals("Intersection with empty set should be"
				+ " empty", VersionList.intersection(a, b),
				new ArrayList<Integer>());
	}
	
	@Test
	public void intersection() {
		final List<Integer> a = new ArrayList<Integer>();
		final List<Integer> b = new ArrayList<Integer>();
		
		a.add(1);
		a.add(3);
		a.add(4);
		a.add(11);
		
		b.add(5);
		b.add(11);
		
		final List<Integer> ret = new ArrayList<Integer>();
		ret.add(11);
		
		assertEquals("Intersection didn't work", ret, VersionList.intersection(a, b));
	}
}
