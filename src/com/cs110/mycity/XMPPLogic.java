package com.cs110.mycity;

import org.jivesoftware.smack.XMPPConnection;
/*
 *  XMPPLogic is an example of the singleton pattern. We use this
 *  to coordinate chat connection actions across the main/login,
 *  map, and chat activities.
 */
public class XMPPLogic {
	private XMPPConnection connection = null;
	private static XMPPLogic instance = null;

	public synchronized static XMPPLogic getInstance() {
		if(instance==null){
			instance = new XMPPLogic();
		}
		return instance;
	}

	public void setConnection(XMPPConnection connection){
		this.connection = connection;
	}

	public XMPPConnection getConnection() {
		return this.connection;
	}

}
