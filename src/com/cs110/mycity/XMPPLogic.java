package com.cs110.mycity;

import org.jivesoftware.smack.XMPPConnection;

public class XMPPLogic {
	private static XMPPConnection connection = null;
	private static XMPPLogic instance = null;

	public synchronized static XMPPLogic getInstance() {
		if(instance==null){
			instance = new XMPPLogic();
		}
		return instance;
	}

	public void setConnection(XMPPConnection connection){
		XMPPLogic.connection = connection;
	}

	public static XMPPConnection getConnection() {
		return connection;
	}

}
