package com.cs110.mycity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {
	private static final String TAG = "CHATSERVICE";
	
	private ChatManager chatmanager = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Chatservice called");
		XMPPConnection connection = XMPPLogic.getInstance().getConnection();
		chatmanager = connection.getChatManager();
		chatmanager.addChatListener(
			    new ChatManagerListener() {
			        @Override
			        public void chatCreated(Chat chat, boolean createdLocally)
			        {
			            if (!createdLocally)
			                chat.addMessageListener(new MessageListener() {
			                    @Override
								public void processMessage(Chat chat, Message message) {
			                    	if(message.getBody() != null) {
				                    	ChatHelper chatHelper = ChatHelper.getInstance();
				                    	String from = message.getFrom();
				                    	String text = from + ": " + message.getBody();
				                    	chatHelper.storeMessage(from.substring(0, from.indexOf('/')), text);
			                    	}
			                    }
			                });
			        }
			    });
	}
	
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroying chat service");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "Starting chat service");
	}

}
