package com.cs110.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import android.location.Location;

import com.cs110.mycity.MyOverlay;
import com.cs110.mycity.XMPPLogic;
import com.google.android.maps.GeoPoint;
public class MyOverlayTest extends TestCase {

	MyOverlay l;
	
	public void testSize() {
		ArrayList<Integer> o = new ArrayList<Integer>();
		assertEquals(o.size(), 0);
		o.add(1);
		o.add(10);
		o.add(50);
		o.add(39);
		assertEquals(o.size(), 4);
		o.remove(1);
		assertEquals(o.size(), 3);	
	}


	public void testAddOverlay() {
		ArrayList<String> buddies = new ArrayList<String>();
		buddies.add("Jen");
		buddies.add("Bill");
	    buddies.add("Ian");
	    buddies.add("Kyle");
	    buddies.add("Liz");
	    assertEquals(buddies.size(), 5);
	}

}
