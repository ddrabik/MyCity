package com.cs110.tests;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import junit.framework.TestCase;

import com.cs110.mycity.XMPPLogic;

public class XMPPLogicTests extends TestCase {
	public static final String HOST = "talk.google.com";
	public static final int PORT = 5222;
	public static final String SERVICE = "gmail.com";
	
	public void testInstance(){
		XMPPLogic xmpp = XMPPLogic.getInstance();		
		assertNotNull(xmpp);
	}

	public void testConnection(){
		XMPPLogic xmpp = XMPPLogic.getInstance();		
		ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT, SERVICE);
		XMPPConnection connection = null;	
		connection = new XMPPConnection(connConfig);
		xmpp.setConnection(connection);
		assertEquals(connection, XMPPLogic.getConnection());
	}
}
