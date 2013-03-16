package com.cs110.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.location.Location;

import com.cs110.mycity.MapHelper;
import com.cs110.mycity.MapService;
import com.cs110.mycity.MappingActivity;
import com.cs110.mycity.XMPPLogic;
import com.google.android.maps.GeoPoint;

public class MapHelperTests extends TestCase {

	private static final String HOST = "talk.google.com";
	private static final int PORT = 5222;
	private static final String SERVICE = "gmail.com";
	private String mEmail = "cse110winter2013@gmail.com";
	private String mPassword = "billgriswold";
	private static final Integer BUDDY_AVAILABLE = new Integer(1);
	private static final Integer BUDDY_UNAVAILABLE = new Integer(2);
	private XMPPConnection connection;
	
	private static MapService chatService = MapService.getInstance();
	MapHelper mh;
	
	public void testGetInstance() {
		MapHelper mh = MapHelper.getInstance();
		assertNotNull(mh);
	}

	public void testSendMessageTo() {
		MapHelper mh = MapHelper.getInstance();
		Location buddyLoc = new Location("");
		ArrayList<String> buddy = chatService.getBuddyList();
	    GeoPoint currentPoint = new GeoPoint((int)34.5237,(int)-113.188774);
	    Location newLocation = new Location("");
	    newLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
	    newLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	    mh.sendMessageTo(buddy.toString(),newLocation);
	}

	public void testSendToAllBuddies() {
		MapHelper mh = MapHelper.getInstance();
		Location buddyLoc = new Location("");
		ArrayList<String> buddies = chatService.getBuddyList();
	    GeoPoint currentPoint = new GeoPoint((int)34.5237,(int)-113.188774);
	    Location newLocation = new Location("");
	    newLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
	    newLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	    mh.sendMessageTo(buddies.toString(),newLocation);
	}

	public void testSendToAllUsers() {
		MapHelper mh = MapHelper.getInstance();
		Location buddyLoc = new Location("");
		ArrayList<String> allUsers = chatService.getBuddyList();
	    GeoPoint currentPoint = new GeoPoint((int)34.5237,(int)-113.188774);
	    Location newLocation = new Location("");
	    newLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
	    newLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	    mh.sendMessageTo(allUsers.toString(),newLocation);
	}

	public void testReceivedLocationFrom() {
		 String str3 = "Location received from buddy";
		 XMPPConnection connection = login();
		 assertNotNull(connection);
		 MapHelper mh = MapHelper.getInstance();
		 HashMap<String, Location> buddyList = mh.getBuddyLocations();
		 Location loc;
		 if(buddyList.size() != 0)
		   loc = buddyList.get(0);
		 else
		   return;
		HashMap<String, Location> convo = mh.getBuddyLocations();
		convo = mh.getBuddyLocations();
		Location recent = convo.get(convo.size()-1);
		assertEquals(loc + ": " + str3, recent);
	}

	public void testGetBuddyLocations() {
	   Location buddyLoc = new Location("");
	   String buddies;
       GeoPoint currentPoint = new GeoPoint((int)34.5237,(int)-113.188774);
       Location newLocation = new Location("");
       newLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
       newLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	}
	
	public XMPPConnection login() {
		XMPPConnection connection = null;
		ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
		connection = new XMPPConnection(connConfig);
		try {
		    connection.connect();
	     } catch (XMPPException ex) {
		       ex.printStackTrace();
	           return null;
		 }
		try {
		connection.login(mEmail, mPassword);
	    } catch (XMPPException ex) {
		    ex.printStackTrace();
		    return null;
		}
		XMPPLogic.getInstance().setConnection(connection);
		return connection;
		}

}
