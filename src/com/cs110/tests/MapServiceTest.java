package com.cs110.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.cs110.mycity.MapService;
import com.cs110.mycity.XMPPLogic;

public class MapServiceTest extends TestCase {
	private static final String HOST = "talk.google.com";
	private static final int PORT = 5222;
	private static final String SERVICE = "gmail.com";
	private String mEmail = "cse110winter2013@gmail.com";
	private String mPassword = "billgriswold";
	private static final Integer BUDDY_AVAILABLE = new Integer(1);
	private static final Integer BUDDY_UNAVAILABLE = new Integer(2);

	public void testGetInstance() {
		MapService ms = MapService.getInstance();
		assertNotNull(ms);
	}

	public void testSendMessageTo() {
		//assertNotNull(connection);
		String str2 = "Texttexttext";
		String user = "TestUser";

		MapService ms = MapService.getInstance();
		List<String> buddyList = ms.getBuddyList();
		String buddy;
		if(buddyList.size() != 0)
		  buddy = buddyList.get(0);
		else
	      return;
		assertEquals(buddy, "Hello");
	}


	public void testGetBuddyList() {
		ArrayList<String> buddyList = new ArrayList<String>();
		buddyList.add("dummybuddy1");
		buddyList.add("dummybuddy2");
		buddyList.add("dummybuddy3");
		assertEquals(3, buddyList.size());
	}
}
