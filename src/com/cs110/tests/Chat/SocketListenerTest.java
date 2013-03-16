package com.cs110.tests.Chat;

import java.util.Collection;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.util.Log;

import com.cs110.mycity.XMPPLogic;
import com.cs110.mycity.Chat.SocketListener;

public class SocketListenerTest extends ServiceTestCase {
    private static final String TAG = "SOCKETLISTENERTEST";
	private static final String HOST = "talk.google.com";
	private static final int PORT = 5222;
	private static final String SERVICE = "gmail.com";
	private XMPPConnection connection = null;
	private IBinder binder = null;
	private SocketListener mService = null;
	
	public SocketListenerTest() {
		super(SocketListener.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		XMPPLogic xmpp = XMPPLogic.getInstance();		
		ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
		connection = new XMPPConnection(connConfig);
		connection.connect();
		connection.login("cse11test@gmail.com", "cse11test1");
		xmpp.setConnection(connection);
		binder = bindService(new Intent(this.getSystemContext(), SocketListener.class));
		mService = ((SocketListener.LocalBinder) binder).getService();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		connection = null;
		binder = null;
		mService = null;
	}
	
	public void testGetBuddyList() {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		assertEquals(entries.size(), mService.getBuddyList().size());
	}
	
	public void testGetConversationWith() {
		assertEquals(0, new SocketListener().getConversationWith(null).size());
		
		assertEquals(0, new SocketListener().getConversationWith("asdfasdf").size());
		
		mService.sendMessageTo("buddy", "test message");
		assertEquals(1, mService.getConversationWith("buddy").size());
	}
	
	public void testGetLastMessage() {
		assertEquals("", new SocketListener().getLastMessage());
	}
	
	public void testGetLastBuddy() {
		assertEquals("", new SocketListener().getLastBuddy());
	}
	
	public void testDidRecieveMessage() {
		assertEquals(false, new SocketListener().didRecieveMessage());
	}
	
	

}
