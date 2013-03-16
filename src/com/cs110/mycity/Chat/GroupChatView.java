package com.cs110.mycity.Chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cs110.mycity.R;
import com.cs110.mycity.R.id;
import com.cs110.mycity.R.layout;
import com.cs110.mycity.XMPPLogic;

public class GroupChatView extends Activity{

    private MultiUserChat 				muc;
    private XMPPConnection 				connection;
//    private Set<String>					buddyList;
    private ArrayList<String> 			messages;
//    private Iterator<String>			buddies;
    private String						useremail;
    private EditText					msg;
    private ListView					msglist;
    private Message						lastmsg;
//    private int							count;
    private boolean						activity_active;
    
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupchat);
        connection = XMPPLogic.getConnection();
        useremail = connection.getUser();
//        buddyList = new Set<String>();
        messages = new ArrayList<String>();
 //       count = 0;
        
        
        Log.i("GroupChatView", "On Create");
       
        if (GroupChatController.join)
        {
           Log.i("GroupChatActivity", "On Create Group join room: "+ GroupChatController.room);
           GroupChatController.joinGroupChat(GroupChatController.room);
        }
        
        
        msg = (EditText) this.findViewById(R.id.groupchatET);
        msglist = (ListView) this.findViewById(R.id.grouplistMessages);
        muc = GroupChatController.muc;
        
        Log.i("GroupChatActivity", "Try Create List");
        
//        try
//        {
//            CreateList();
//        } catch (XMPPException e)
//        {
//            e.printStackTrace();
//        }
        Log.i("GroupChatActivity", "Set Up Receive Message");
        receiveMessage();
        Log.i("GroupChatActivity", "Set Up Sent Listener");
        setSend();
        lastmsg= null;
//        activity_active = true;
//        myCityService.service_notification = false;
//        MyCityUtils.setMyCity_active(true);
        
        Log.i("GroupChatActivity", "Finish On Create");
    }
    
//    private void CreateList() throws XMPPException
//    {
//        String buddy;
//        if (!buddyList.isEmpty())
//            Log.i("GroupChatActivity", "Remove User List: "+buddyList.removeAll(buddyList));
//        // TODO Auto-generated method stub
//        Log.i("GroupChatActivity", "Get Buddy In Room");
//        buddies = GroupChatController.muc.getOccupants();
//        Log.i("GroupChatActivity", "Get Count");
//        count = GroupChatController.muc.getOccupantsCount();
//        Log.i("GroupChatActivity", "While People has next + OccupantsCount: "+ count);
//        while (buddies.hasNext())
//        {
//            buddy = StringUtils.parseResource(buddies.next());
//            Log.i("GroupChatActivity", "People: "+ buddy);
//            buddyList.add(buddy);
//        }
//        Log.i("GroupChatActivity", "Finished CreateList");
//    }

    private void receiveMessage()
    {
        // TODO Auto-generated method stub
        
        muc.addMessageListener(new PacketListener(){

            @Override
            public void processPacket(Packet arg0)
            {
                final Message msg = (Message) arg0;

                // Give ourselves a nice chat message
                if (msg.getBody() != null) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("Group Chat R", "Group Message Received: " + msg.getBody());
                            Log.i("Group Chat R", "Group Message Subject: " + msg.getSubject());
                            Log.i("Group Chat R", "Group Message Subject: " + msg.getFrom());
                            if (!msg.getBody().equals("nullnullnullnull"))
                            {
                                messages.add(parseMessage(msg));
                                setListAdapter();
                            }
                        }

                    });
                }
            }
            
        });
        
    }

    private void setSend()
    {
        // TODO Auto-generated method stub
        Button send = (Button) this.findViewById(R.id.groupsendBtn);
        send.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                String text = msg.getText().toString();
                msg.setText("");
                if (!text.equals("")) {
                    Log.i("GroupChat", "Sending group message: "+ text);
                    try
                    {
                        Message msg = muc.createMessage();
                        msg.setBody(text);
                        msg.setSubject(useremail);
                        muc.sendMessage(msg);
                        //sentMessageUpdate(msg);
                    } catch (XMPPException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        
    }

    public void sentMessageUpdate(Message msg)
    {
        String save= parseMessage(msg);
        if (save != null)
            if (!save.equals(""))
            {
                messages.add(parseMessage(msg));
            
                runOnUiThread(new Runnable() {
                    public void run() {
                        setListAdapter();
                    }
                });
            }
    }

    protected void setListAdapter()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
        msglist.setAdapter(adapter);
        msglist.invalidate();
        if (msglist.getAdapter().getCount()> 0)
        	msglist.setSelection(msglist.getAdapter().getCount() - 1);
    }

    private String parseMessage(Message msg)
    {
        String res= "";
        if (lastmsg!= null){
            Log.d("Debug Receive", "Pass lastmsg != null");
            if (lastmsg.getSubject() != null && msg.getSubject() !=null){
              Log.d("Debug Receive", "Pass subject != null");
              if (!lastmsg.getSubject().equals(msg.getSubject())) {
                res = msg.getSubject() + ":\n\t" + msg.getBody();
              } 
              else {
               res = "\t" + msg.getBody();
              }
            }
            else
                if (lastmsg.getSubject() == null || msg.getSubject() == null)
                    res = msg.getSubject() + ":\n\t" + msg.getBody();
                    
        }
        else
        {
            res = msg.getSubject() + ":\n\t" + msg.getBody();
        }
        this.lastmsg = msg;
        return res;
    }
    
    @Override
    protected void onPause() {
 //       this.activity_active = false;
  //      myCityService.service_notification= true;
  //      MyCityUtils.setMyCity_active(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        GroupChatController.muc.leave();
        GroupChatController.muc = null;
        muc.leave();
        muc = null;
        super.onDestroy();
    }
    

    public void receivedMessageUpdate(Message msg) {
        // Only show the toast if we are the current activity
        if (this.activity_active) {
            runOnUiThread(new Runnable() {
 //               connection = XMPPLogic.getConnection();
 //               useremail = connection.getUser();
                
                @Override
                public void run() {
 //                   Toast.makeText(getApplicationContext(), "New message from: " + msg.getSender(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "New message from buddy", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
