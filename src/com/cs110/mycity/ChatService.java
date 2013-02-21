package com.cs110.mycity;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {
	private static final String TAG = "CHATSERVICE";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		createChat();
	}
	
	private void createChat() {
		XMPPConnection connection = XMPPLogic.getInstance().getConnection();
		ChatManager chatmanager = connection.getChatManager();
		chatmanager.addChatListener(
			    new ChatManagerListener() {
			        @Override
			        public void chatCreated(Chat chat, boolean createdLocally)
			        {
			            if (!createdLocally)
			                chat.addMessageListener(new MessageListener() {
			                    public void processMessage(Chat chat, Message message) {
			                    	Log.d(TAG, chat.getThreadID());
			                    	Log.d(TAG, chat.getParticipant());
			                        Log.d(TAG, "Received message: " + message.getBody().toString());
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
