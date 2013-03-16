package com.cs110.mycity.Chat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cs110.mycity.R;

public class ChatView extends Activity implements Observer {
	public static final String TAG = "CHATVIEW";
	private Handler mHandler = new Handler();
	SocketListener mService = null;
	ChatController controller;

	ArrayList<String> history = new ArrayList<String>();
	String buddy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chat);
		this.buddy = getPartnerName();
		setConversationPartnerBanner("Chatting with ");
		setInputTextBoxHint("Chat ");

		activateSendButton();
	}

	private void setConversationPartnerBanner(String prefix) {
		TextView buddyTextView = (TextView) this.findViewById(R.id.buddy);
		buddyTextView.setText(prefix + this.buddy);
	}

	private String getPartnerName() {
		String buddy = "A buddy";
		Bundle extras = getIntent().getExtras();
		if( extras != null ) {
			buddy = extras.getString("SELECTED_BUDDY");
		}
		return buddy;
	}

	private void activateSendButton() {
		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendMessageTo(getInputBoxText());
				resetInputTextBox();
			}
		});
	}

	private String getInputBoxText() {
		return getInputBox().getText().toString();
	}

	private void resetInputTextBox() {
		getInputBox().setText("");
	}

	private void setInputTextBoxHint(String hint) {
		getInputBox().setHint("Chat ");
	}

	private EditText getInputBox() {
		return (EditText) this.findViewById(R.id.chatET);
	}


	private void sendMessageTo(String text) {
		controller.sendMessage(text);
	}

	/**
	 * Class for interacting with the chat model.
	 */
	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.d(TAG, "Connected to service.");
			mService = ((SocketListener.LocalBinder) binder).getService();
			mService.registerObserver(ChatView.this);
			controller = new ChatController(ChatView.this, getPartnerName(), mService);
			update(mService);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Disconnected from service.");
			mService = null;
		}
	};

	@Override
	public void update(Subject s) {
		Log.d(TAG, "Updating view");
		history = s.getConversationWith(this.buddy);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				display();
			}
		});

	}

	private void display() {
		setListAdapter(this.history);
	}

	private void setListAdapter(ArrayList<String> history) {
		if(history != null) {
			ListView listview = (ListView) this.findViewById(R.id.listMessages);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, history);
			listview.setAdapter(adapter);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, SocketListener.class), mConn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		mService.removeObserver(ChatView.this);
		controller.conversationViewed();
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConn);
	}

}
