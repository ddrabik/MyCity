package com.cs110.mycity;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class ChatHelper {
	private static ChatHelper mInstance = null;
	private XMPPConnection connection = null;
	
	public synchronized static ChatHelper getInstance() {
	    if(mInstance==null){
	      mInstance = new ChatHelper();
	    }
	    return mInstance;
	}
	
	private ChatHelper() {
		connection = XMPPLogic.getInstance().getConnection();
	}
	
	
	
	public void sendMessageTo(String user, String body) {
		Message msg = new Message(user, Message.Type.chat); 
		msg.setBody(body);
		
		if(connection != null) {
			connection.sendPacket(msg);
		}
	}
	
	public void sendLocationTo(String user, String coordinates) {
		String location = "{{location:" + coordinates + "-122,10}}";
		this.sendMessageTo(user, location);
	}
	
}
