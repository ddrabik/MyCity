package com.cs110.mycity;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
/*
 * ChatService handles the chat service functionality in our app.
 * It handles the sending and receiving of messages.
 */
public class ChatService extends Service {
	private static final String TAG = "CHATSERVICE";

	private ChatManager chatmanager = null;
	private XMPPConnection connection;

	private static ChatService mInstance;
	
	private  MapHelper mapHelper;

	public synchronized static ChatService getInstance() {
		if(mInstance==null){
			mInstance = new ChatService();
		}
		return mInstance;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/* When the user first logs in, the chat service is created and is
	 * always running, listening for messages that are received from a
	 * buddy.
	 */
	public void onCreate() {
		mInstance = ChatService.this;
		mapHelper = MapHelper.getInstance();

		connection = XMPPLogic.getInstance().getConnection();
		chatmanager = connection.getChatManager();
		chatmanager.addChatListener(
				new ChatManagerListener() {
					@Override
					public void chatCreated(Chat chat, boolean createdLocally)
					{
						if (!createdLocally)
							chat.addMessageListener(new MessageListener() {
								@Override
								// process a new received message from a buddy
								public void processMessage(Chat chat, Message message) {
									if(message.getBody() != null) {
										ChatHelper chatHelper = ChatHelper.getInstance();
										String from = message.getFrom();
										from = from.substring(0, from.indexOf('/'));
										Log.d(TAG, "Received from " + from + ": " + message.getBody());	
										chatHelper.newMessageReceived(from, message.getBody());	
									
										if(message.getBody().startsWith("<trkp")){
											Log.d(TAG, "Sending location to MAP HELPER from...  "	+ from);
											
											mapHelper.receivedLocationFrom(from, message.getBody());
											
										}
									
									
									
									
									}
									
									
									
									
									
								}
							});
					}
				});
	}
	/* send a new message to a buddy
	 */
	public void sendMessageTo(String buddy, String text) {
		Message msg = new Message(buddy, Message.Type.chat); 
		msg.setBody(text);
		Log.d(TAG, "Sending to " + buddy + ": " + text);	
		if(connection != null) {
			connection.sendPacket(msg);
		}
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Destroying chat service");
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.d(TAG, "Starting chat service");
	}
	
	
	
	public ArrayList<String> getBuddyList() {
		
		ArrayList<String> buddies = new ArrayList<String>();
		
		if( connection != null ) {
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				String buddy = entry.getUser();
				buddies.add(buddy);
			}
		
		}
		
		return buddies;
		
	}
	
	
	

}
