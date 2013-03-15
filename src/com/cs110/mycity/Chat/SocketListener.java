package com.cs110.mycity.Chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cs110.mycity.R;
import com.cs110.mycity.XMPPLogic;

public class SocketListener extends Service implements Subject {
	private static final String TAG = "SOCKETLISTENER";
	private XMPPConnection connection = XMPPLogic.getInstance().getConnection();
	
	private ArrayList<Observer> observers = new ArrayList<Observer>();

	private HashMap<String, Chat> chatDB = new HashMap<String, Chat>();
	private HashMap<String, ArrayList<String>> conversationHistory = new HashMap<String, ArrayList<String>>();
	private HashMap<String, Integer> buddyStatuses = new HashMap<String, Integer>();
	
	private String lastMessage = "";
	private String lastBuddy = "";
	private Boolean receivedMessage = false;
	
	private NotificationObserver notificationObserver;

	@Override
	public void onCreate() {
		if(connection != null) {
			Log.d(TAG, "socketlistener is live");
			addChatListener(connection.getChatManager());
			addRosterListener(connection.getRoster());
			notificationObserver = new NotificationObserver(this);
			registerObserver(notificationObserver);
			getInitialBuddyList();
		}
	}	

	public void onDestroy() {
		super.onDestroy();
	}

	public ArrayList<String> getBuddyList() {
		ArrayList<String> list = new ArrayList<String>();

		Iterator<Map.Entry<String, Integer>> it = buddyStatuses.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Integer> pairs = it.next();
			Integer status = pairs.getValue();
			list.add(status.toString() + pairs.getKey());
		}
		Collections.sort(list);
		return list;
	}
	
	public void sendMessageTo(String buddy, String text) {
		Message msg = createMessage(buddy, connection.getUser(), text);
		Chat chat = getChat(buddy);
		try {
			chat.sendMessage(text);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		updateHistory(chat, msg);
	}
	
	public ArrayList<String> getConversationWith(String buddy) {
		if(buddy == null || conversationHistory.get(buddy) == null) {
			return new ArrayList<String>();
		}
		return conversationHistory.get(buddy);
	}

	public String getLastMessage() {
		return this.lastMessage;
	}
	
	public String getLastBuddy() {
		return this.lastBuddy;
	}
	
	private void setLatestMessageInfo(String buddy, String text) {
		setRecievedMessage(true);
		setLastBuddy(cleanUserString(buddy));
		setLastMessage(text);
	}
	
	private void setLastBuddy(String buddy) {
		this.lastBuddy = buddy;
	}
	
	private void setLastMessage(String msg) {
		this.lastMessage = msg;
		Log.d(TAG, this.lastMessage);
	}
	
	private void setRecievedMessage(boolean b) {
		this.receivedMessage = b;
	}
	
	public boolean didRecieveMessage() {
		return this.receivedMessage;
	}
	
	public void resetBuddyPresence(String user) {
		buddyStatuses.put(cleanUserString(user), new Integer(1));
	}
	
	public void registerObserver(Observer o) {
		observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		Log.d(TAG, "removed observer");
		observers.remove(o);
	}
	
	private void notifyObservers() {
		Iterator<Observer> it = observers.iterator();
		
		while(it.hasNext()) {
			Observer o = it.next();
			o.update(this);
		}
		setRecievedMessage(false);

	}

	public class LocalBinder extends Binder {

		public SocketListener getService() {
			return SocketListener.this;
		}

	}

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void addChatListener(ChatManager chatmanager) {
		chatmanager.addChatListener( new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				if (!createdLocally) {
					chat.addMessageListener(createMessageListener());
				} else {
					Log.d(TAG, "Chat not created locally");
				}
			}
		});
	}
	
	private MessageListener createMessageListener() {
		MessageListener listener = new MessageListener() {
			public void processMessage(Chat chat, Message message) {
				if(message.getBody() != null && ! message.getBody().startsWith("<trkp")) {
					setLatestMessageInfo(chat.getParticipant(), message.getBody());
					setBuddyPresence(chat.getParticipant(), new Integer(0));
					updateChat(chat, message);
				}
			}
		};
		return listener;
	}
	
	private void updateChat(Chat chat, Message message) {
		String participant = chat.getParticipant();
		addChatToDB(participant, chat);
		updateHistory(chat, message);
	}

	private void updateHistory(Chat chat, Message message) {
		String user = cleanUserString(chat.getParticipant());
		ArrayList<String> chatHistory = getConversationHistory(user);
		chatHistory.add(cleanUserString(message.getFrom()) + ": " + message.getBody());
		notifyObservers();
		Log.d(TAG, "History updated, added " + cleanUserString(user) + ": " + message.getBody());
	}
	
	private ArrayList<String> getConversationHistory(String user) {
		ArrayList<String> chatHistory = conversationHistory.get(user);
		if(chatHistory == null) {
			chatHistory = new ArrayList<String>();
			conversationHistory.put(user, chatHistory);
		}
		return conversationHistory.get(user);
	}
	
	private String cleanUserString(String user) {
		if( user.lastIndexOf('/') > 0 ) {
			return user.substring(0, user.indexOf('/'));
		} else {
			return user;
		}
	}

	private Chat getChat(String buddy) {
		Chat chat = chatDB.get(buddy);
		if(chat == null) {
			chat = createNewChat(buddy);
		}
		return chat;
	}
	
	private Chat createNewChat(String buddy) {
		Log.d(TAG, "new chat created");
		ChatManager manager = connection.getChatManager();
		Chat chat = manager.createChat(buddy, createMessageListener());
		addChatToDB(buddy, chat);
		return chat;
	}
	
	private void addChatToDB(String buddy, Chat chat) {
		Log.d(TAG, "Adding chat with participant " + buddy);
		chatDB.put(cleanUserString(buddy), chat);
	}
	
	private Message createMessage(String to, String from, String text) {
		Message msg = new Message();
		msg.setTo(to);
		msg.setFrom(from);
		msg.setBody(text);
		return msg;
	}

	private void addRosterListener(Roster roster) {
		roster.addRosterListener(new RosterListener() {

			@Override
			public void entriesAdded(Collection<String> addresses) {}

			@Override
			public void entriesDeleted(Collection<String> addresses) {}

			@Override
			public void entriesUpdated(Collection<String> addresses) {}

			@Override
			public void presenceChanged(Presence presence) {
				setBuddyPresence(presence.getFrom(), convertPresenceToStatusCode(presence));
			}

		});
	}

	private void setBuddyPresence(String user, Integer status) {
		user = cleanUserString(user);
		if(buddyStatuses.get(user) == null || !buddyStatuses.get(user).equals(new Integer(0))) {
			buddyStatuses.put(user, status);
		}
	}
	
	private Integer convertPresenceToStatusCode(Presence p) {
		return (p.isAvailable()) ? new Integer(1) : new Integer(2);
	}
	
	private void getInitialBuddyList() {
		Log.d(TAG, "Getting inital buddy list");
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			String user = entry.getUser();
			Presence presence = roster.getPresence(user);
			setBuddyPresence(presence.getFrom(), convertPresenceToStatusCode(presence));
		}
	}

	public NotificationObserver getNotificationObserver() {
		return this.notificationObserver;
	}

}

