package com.cs110.mycity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;

public class ChatHelper {
	private static final String TAG = "CHATHELPER";
	private static ChatHelper mInstance = null;
	private XMPPConnection connection = null;
	
	private Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
	
	public synchronized static ChatHelper getInstance() {
	    if(mInstance==null){
	      mInstance = new ChatHelper();
	    }
	    return mInstance;
	}
	
	private ChatHelper() {
		connection = XMPPLogic.getInstance().getConnection();
	}
	
	
	
	public void sendMessageTo(String buddy, String from, String text) {
		Message msg = new Message(buddy, Message.Type.chat); 
		msg.setBody(text);
		
		if(connection != null) {
			connection.sendPacket(msg);
			storeMessage(buddy, from + ": " + text);
		}
	}
	
	public void sendLocationTo(String user, String coordinates) {
		String location = "{{location:" + coordinates + "-122,10}}";
//		this.sendMessageTo(user, location);
	}

	public void storeMessage(String buddy, String text) {
		if(map.get(buddy) == null) {
			map.put(buddy, new ArrayList<String>());
		}
		
		ArrayList<String> messages = map.get(buddy);
		messages.add(text);
	}
	
	public ArrayList<String> getMessages(String buddy) {
		if(map.get(buddy) == null) {
			Log.d(TAG, "Returning empty arraylist");
			ArrayList<String> arr = new ArrayList<String>();
			arr.add("");
			return arr;
		} else {
			Log.d(TAG, "Returning arraylist of size " + map.get(buddy).size() + " for " + buddy);
			for( int i = 0; i < map.get(buddy).size(); i++) {
				Log.d(TAG, "Entry: " + map.get(buddy).get(i));
			}
			return map.get(buddy);
		}
	}
	
}
