package com.cs110.mycity;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BuddyChat extends Activity {
 
	  protected static final String TAG = "TAAAAAG";
	  private XMPPConnection connection;
	  private ArrayList<String> messages = new ArrayList<String>();
	  private Handler mHandler = new Handler();

	  private EditText recipient;
	  private EditText textMessage;
	  private ListView listview;
	   private String recipientName;
	   
	   

	
	  
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Log.d(TAG, "CALLING ON CREATE");
	    setContentView(R.layout.chat);

	    TextView conversationPartner = (TextView) this.findViewById(R.id.buddy);
	    Bundle extras = getIntent().getExtras();
	    if( extras != null ) {
	    	conversationPartner.setText("Chatting with " + extras.getString("SELECTED_BUDDY"));
	    	this.recipientName = extras.getString("SELECTED_BUDDY");
	    }
	    
	    
	    
	    
	    this.connection = XMPPLogic.getInstance().getConnection();
	    this.textMessage = (EditText) this.findViewById(R.id.chatET);
	    textMessage.setHint("Chat ");
	    this.listview = (ListView) this.findViewById(R.id.listMessages); //is this necessary?
	    
	    
//	    Display display = getWindowManager().getDefaultDisplay();
//	    Point size = new Point();
//	    display.getSize(size);
//	    int width = size.x;
//	    int height = size.y;
//	    this.listview.setVerticalScrollBarEnabled(true);
//	    
//	    
//	    
        
        Button send = (Button) this.findViewById(R.id.sendBtn);
        send.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {
            String to = recipientName;
            String text = textMessage.getText().toString();          
           
            Log.i("XMPPChatDemoActivity ", "Sending text " + text + " to " + to);
            Message msg = new Message(to, Message.Type.chat);  
           
            msg.setBody(text);
            if (connection != null) {
              connection.sendPacket(msg);
              messages.add(connection.getUser() + ":");
              messages.add(text);
              setListAdapter();
              
              //reset edit textbox
              textMessage.setText("");
            }
          }
        });
        
        
        
        
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
              @Override
              public void processPacket(Packet packet) {
               
              	Message message = (Message) packet;
                if (message.getBody() != null && StringUtils.parseBareAddress(message.getFrom()).equals(recipientName)) {
             
                  String fromName = StringUtils.parseBareAddress(message.getFrom());
                  
                  Log.d(TAG,fromName);
                  
                  Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);
                 
                  String newBody = MySmack.processAppointment(packet);
                  messages.add(fromName + ":");
                  messages.add(newBody);
                  // Add the incoming message to the list view
                  mHandler.post(new Runnable() {
                    public void run() {
                      setListAdapter();
                    }
                  });
                }
              }
            }, filter);
          }
        
        
        
        
        
        
        

	
	}
	
	
	
	
	
	private void setListAdapter() {
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
	    listview.setAdapter(adapter);
	  }

	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "CALLED ON RESUME");
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "CALLED ON PAUSE");

	}
	
	@Override
	public void onStop(){
		super.onStop();
		Log.d(TAG, "CALLED ON STOP");

		
	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG, "CALLED ON START");
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "CALLED ON DESTROOOOY");
	}
	
}
