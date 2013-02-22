package com.cs110.mycity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

public class ChatHelper {
	private static final String TAG = "CHATHELPER";
	private static final Integer BUDDY_AVAILABLE = new Integer(1);
	private static final Integer BUDDY_UNAVAILABLE = new Integer(2);
	private static final Integer BUDDY_MESSAGE_WAITING = new Integer(0);

	private static ChatHelper mInstance = null;
	private XMPPConnection connection = null;

	private HashMap<String, Integer> buddyList = new HashMap<String, Integer>();
	private HashMap<String, ArrayList<String>> msgDB = new HashMap<String, ArrayList<String>>();

	public synchronized static ChatHelper getInstance() {
		if(mInstance==null){
			mInstance = new ChatHelper();
		}
		return mInstance;
	}

	private ChatHelper() {
		connection = XMPPLogic.getInstance().getConnection();
		updateBuddyList();
	}

	public void sendMessageTo(String buddy, String from, String text) {
		ChatService.getInstance().sendMessageTo(buddy, text);
		storeMessage(buddy, from + ": " + text);
	}
	

	public void storeMessage(String buddy, String text) {
		if(msgDB.get(buddy) == null) {
			msgDB.put(buddy, new ArrayList<String>());
		}

		ArrayList<String> messages = msgDB.get(buddy);
		messages.add(text);
	}

	public void newMessageReceived(String from, String text) {
		storeMessage(from, from + ": " + text);
		buddyList.put(from, BUDDY_MESSAGE_WAITING);
	}

	public ArrayList<String> getConversationWith(String buddy) {
		return (msgDB.get(buddy) != null) ? msgDB.get(buddy) : new ArrayList<String>();
	}

	public void viewedConversationWith(String buddy) {
		buddyList.put(buddy, BUDDY_AVAILABLE);
	}

	public ArrayList<String> getBuddyList() {
		updateBuddyList();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<Map.Entry<String, Integer>> it = buddyList.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			list.add(pairs.getValue().toString() + pairs.getKey());
		}
		Collections.sort(list);
		return list;
	}
	
	private void updateBuddyList() {
		if( connection != null ) {
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				String buddy = entry.getUser();

				Presence entryPresence = roster.getPresence(buddy);
				Presence.Type type = entryPresence.getType();
				Integer statusCode = (type == Presence.Type.available) ? BUDDY_AVAILABLE : BUDDY_UNAVAILABLE;

				if(buddyList.get(buddy) == null ||
						!buddyList.get(buddy).equals(BUDDY_MESSAGE_WAITING)) {
					buddyList.put(buddy, statusCode);
				}
			}
		}
	}

}
