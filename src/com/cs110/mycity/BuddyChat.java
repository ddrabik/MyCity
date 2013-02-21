package com.cs110.mycity;

import java.util.ArrayList;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BuddyChat extends Activity {

	protected static final String TAG = "BUDDYCHAT";
	private XMPPConnection connection = XMPPLogic.getInstance().getConnection();
	
	private String buddy;
	private String currentUser;
	private ArrayList<String> conversation;
	private Handler mHandler = new Handler();
	private ChatHelper chatHelper = ChatHelper.getInstance();

	private EditText messageInput;
	private ListView listview;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chat);

		Bundle extras = getIntent().getExtras();
		if( extras != null ) {
			TextView buddyTextView = (TextView) this.findViewById(R.id.buddy);
			buddy = extras.getString("SELECTED_BUDDY");
			buddyTextView.setText("Chatting with " + buddy);
		}

		currentUser = (connection != null) ? connection.getUser() : "a buddy";

		messageInput = (EditText) this.findViewById(R.id.chatET);
		messageInput.setHint("Chat ");

		this.listview = (ListView) this.findViewById(R.id.listMessages); 

		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String text = messageInput.getText().toString();  
				chatHelper.sendMessageTo(buddy, currentUser.substring(0, currentUser.indexOf('/')), text);
				setListAdapter();
				messageInput.setText("");
			}
		});

		setListAdapter();

		// Add a packet listener to get messages sent to us
		PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
		connection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {         
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						setListAdapter();
					}
				});
			}
		}, filter);
	}


	private void setListAdapter() {
		conversation = chatHelper.getConversationWith(buddy);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, conversation);
		listview.setAdapter(adapter);
	}	
	
	public void onPause() {
		chatHelper.viewedConversationWith(buddy);
		super.onPause();
	}

}
