package com.cs110.mycity.Chat;

import java.util.Set;
import java.util.UUID;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.cs110.mycity.XMPPLogic;
import android.util.Log;


public class GroupChatController {
	
    private static XMPPConnection 	connection;
	public static String			room;
	private static String			useremail;
	public static MultiUserChat		muc;
	public static boolean 			join = true;

	    
	    public static void startGroupChat(Set<String> buddies)
	    {
	        connection = XMPPLogic.getConnection();
	        useremail = connection.getUser();
	        room = "private-chat-"+UUID.randomUUID().toString() +"@groupchat.google.com";
	        Log.i("GroupChatController ", "Room name: "+ room);
	        muc = new MultiUserChat(connection, room);
	        try
	        {
	            muc.join(useremail);
	        } catch (XMPPException e)
	        {
	            e.printStackTrace();
	        }
	        for (String buddy:buddies)
	        {
	            Log.i("INVITE", buddy);
	            //muc.invite(buddy, "starting group chat...");
	        }
	        sendInvitation(buddies);    
	        join = false;
	    }
	    
	    public static void joinGroupChat(String room)
	    {
	        connection = XMPPLogic.getConnection();
	        useremail = connection.getUser();
	        GroupChatController.room = room;
	        muc = new MultiUserChat(connection, room);
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
	        // TODO Auto-generated method stub
	        String text= room;
	        for (String buddy: buddies) {
	        	Message msg= new Message(buddy, Message.Type.chat);
	        	msg.setBody("Join our group chat!");
	        	msg.setSubject(text);
	        	XMPPLogic.getConnection().sendPacket(msg);
	        }
	    }

}
