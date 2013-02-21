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

	protected static final String TAG = "BUDDYCHAT";
	private XMPPConnection connection;
	private ArrayList<String> messages;
	private Handler mHandler = new Handler();
	private ChatHelper chatHelper = ChatHelper.getInstance();
	Bundle extras = null;

	private EditText textMessage;
	private ListView listview;
	private String recipientName;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chat);

		TextView conversationPartner = (TextView) this.findViewById(R.id.buddy);
		extras = getIntent().getExtras();
		if( extras != null ) {
			conversationPartner.setText("Chatting with " + extras.getString("SELECTED_BUDDY"));
			this.recipientName = extras.getString("SELECTED_BUDDY");
		}

		this.connection = XMPPLogic.getInstance().getConnection();
		this.textMessage = (EditText) this.findViewById(R.id.chatET);
		textMessage.setHint("Chat ");

		this.listview = (ListView) this.findViewById(R.id.listMessages); 

		if( connection != null) {
			setListAdapter();
		}

		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (connection != null) {

					String to = recipientName;
					String text = textMessage.getText().toString();  
					String from = connection.getUser();
					chatHelper.sendMessageTo(to, from, text);

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
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							setListAdapter();
						}
					});
				}
			}, filter);
		}

	}

	private void setListAdapter() {
		messages = chatHelper.getMessages(extras.getString("SELECTED_BUDDY"));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, this.messages);
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
