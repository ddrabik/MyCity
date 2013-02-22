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

import com.cs110.mycity.ChatHelper;
import com.cs110.mycity.XMPPLogic;

public class ChatHelperTests extends TestCase {
	private static final String HOST = "talk.google.com";
	private static final int PORT = 5222;
	private static final String SERVICE = "gmail.com";
	private String mEmail = "cse110winter2013@gmail.com";
	private String mPassword = "billgriswold";
	private static final Integer BUDDY_AVAILABLE = new Integer(1);
	private static final Integer BUDDY_UNAVAILABLE = new Integer(2);

	public void testInstance() {
		ChatHelper ch = ChatHelper.getInstance();
		assertNotNull(ch);
	}
	
	public void testLogin() {
		XMPPConnection connection = login();
		assertNotNull(connection);		
	}

	public void testBuddyListSize() {
		XMPPConnection connection = login();
		assertNotNull(connection);
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		assertEquals(entries.size(), buddyList.size());
	}
	
	public void testBuddyListContains() {
		XMPPConnection connection = login();
		assertNotNull(connection);
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		List<String> entryList = new ArrayList<String>();
		for (RosterEntry entry : entries) {
			String buddy = entry.getUser();
			entryList.add(buddy);
		}
		for (String buddyStr : buddyList) {
			String buddyEmail = buddyStr.substring(1);
			assertTrue(entryList.contains(buddyEmail));
		}
	}

	public void testMessagingEmpty() {
		XMPPConnection connection = login();
		assertNotNull(connection);
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		String buddy;
		if(buddyList.size() != 0)
			buddy = buddyList.get(0);
		else
			return;
		List<String> convo = ch.getConversationWith(buddy);
		assertEquals(convo.size(), 0);
	}
	
	public void testMessagingStore() {
		String str1 = "Hello buddy!";
		XMPPConnection connection = login();
		assertNotNull(connection);
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		String buddy;
		if(buddyList.size() != 0)
			buddy = buddyList.get(0);
		else
			return;
		List<String> convo = ch.getConversationWith(buddy);
		ch.storeMessage(buddy, str1);
		convo = ch.getConversationWith(buddy);
		assertFalse(ch.getConversationWith(buddy).size() == 0);
		String recent = convo.get(convo.size()-1);
		assertEquals(str1, recent);
	}
	
	public void testMessagingSend() {
		String str2 = "Texttexttext";
		String user = "TestUser";
		XMPPConnection connection = login();
		assertNotNull(connection);
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		String buddy;
		if(buddyList.size() != 0)
			buddy = buddyList.get(0);
		else
			return;
		List<String> convo = ch.getConversationWith(buddy);
		ch.sendMessageTo(buddy, user, str2);
		convo = ch.getConversationWith(buddy);
		String recent = convo.get(convo.size()-1);
		assertEquals(user + ": " + str2, recent);
	}
	
	public void testMessagingReceive() {
		String str3 = "Test message from buddy";
		XMPPConnection connection = login();
		assertNotNull(connection);
		ChatHelper ch = ChatHelper.getInstance();
		List<String> buddyList = ch.getBuddyList();
		String buddy;
		if(buddyList.size() != 0)
			buddy = buddyList.get(0);
		else
			return;
		List<String> convo = ch.getConversationWith(buddy);
		ch.newMessageReceived(buddy, str3);
		convo = ch.getConversationWith(buddy);
		String recent = convo.get(convo.size()-1);
		assertEquals(buddy + ": " + str3, recent);
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
