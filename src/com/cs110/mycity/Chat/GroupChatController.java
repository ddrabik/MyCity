package com.cs110.mycity.Chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.util.Log;

import com.cs110.mycity.XMPPLogic;


public class GroupChatController {
	
    private static final String TAG = "GROUPCHATCONTROLLER";
	private static XMPPConnection 	connection;
	public static String			room;
	private static String			useremail;
	public static MultiUserChat		muc;
	public static boolean 			join = true;
	public static HashMap<String, MultiUserChat> mucDB = new HashMap<String, MultiUserChat>();

	    @SuppressWarnings("static-access")
		public static void startGroupChat(Set<String> buddies)
	    {
	        String roomID = createGroupChatBuddyID(buddies);
	        if(mucDB.get(roomID) == null) {
	        	mucDB.put(roomID, createMultiUserChat(null));
	        }
	        muc = mucDB.get(roomID);
	        try
	        {
	            muc.join(useremail);
	        } catch (XMPPException e)
	        {
	            e.printStackTrace();
	        }
	        sendInvitation(buddies);   
	        SocketListener.addUserToBuddyList(roomID, new Integer(2));
	        join = false;
	    }
	    
	    private static String createGroupChatBuddyID(Set<String> buddies) {
			String id = "";
	    	for(String buddy:buddies) {
				id += buddy + ';';
			}
	    	return id;
		}

		private static MultiUserChat createMultiUserChat(String room) {
	    	connection = XMPPLogic.getConnection();
	        useremail = connection.getUser();
	        if(room == null) {
	        	room = "private-chat-"+UUID.randomUUID().toString() +"@groupchat.google.com";
	        }
	        return new MultiUserChat(connection, room);
	    }
		
		public static void setCurrMUC(String membersString) {
			muc = mucDB.get(membersString);
		}
	    
	    public static void joinGroupChat(String room)
	    {
	    	muc = createMultiUserChat(room);
	        GroupChatController.room = room;
	        
	        
	        try {
				muc.getParticipants();
				Log.d(TAG, "SUCCESS?");
			} catch (XMPPException e1) {
				e1.printStackTrace();
				Log.d(TAG, "Fail");
			}
	        
	        Iterator it = muc.getOccupants();
	        String roomID = "";
	        while(it.hasNext()) {
	        	String occupant = (String) it.next();
	        	Log.d(TAG, occupant);
	        	roomID += occupant + ';';
	        }
	        mucDB.put(roomID, muc);
	        SocketListener.addUserToBuddyList(roomID, new Integer(2));
	        Log.d(TAG, "Put chat with id" + roomID);
	        try
	        {
	            muc.join(useremail);
	        } catch (XMPPException e)
	        {
	            e.printStackTrace();
	        }
	            
	    }
	    
	    public static void sendInvitation(Set<String> buddies)
	    {
	        for (String buddy: buddies) {
	        	muc.invite(buddy, "Join our group chat!");
	        }
	    }

		

}
