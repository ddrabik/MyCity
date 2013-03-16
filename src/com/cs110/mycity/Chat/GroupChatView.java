package com.cs110.mycity.Chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cs110.mycity.R;
import com.cs110.mycity.XMPPLogic;

public class GroupChatView extends Activity {

	protected static final String TAG = "GroupChatView";
	private MultiUserChat muc;
	private XMPPConnection connection;
	private ArrayList<String> messages;
	private String useremail;
	private EditText msg;
	private ListView msglist;
	private Message lastmsg;
	private boolean activity_active;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		
		if( extras != null ) {
			String membersString = extras.getString("ROOM_MEMBERS");
			GroupChatController.setCurrMUC(membersString);
		}
		
		setContentView(R.layout.groupchat);
		connection = XMPPLogic.getConnection();
		useremail = connection.getUser();
		messages = new ArrayList<String>();

//		GroupChatController.joinGroupChat(GroupChatController.room);

		msg = (EditText) this.findViewById(R.id.groupchatET);
		msglist = (ListView) this.findViewById(R.id.grouplistMessages);
		muc = GroupChatController.muc;

		receiveMessage();
		setSend();
		lastmsg = null;

	}

	private void receiveMessage() {
		muc.addMessageListener(new PacketListener() {

			@Override
			public void processPacket(Packet arg0) {
				final Message msg = (Message) arg0;

				if (msg.getBody() != null) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Log.i("Group Chat R", "Group Message Received: "
									+ msg.getBody());
							Log.i("Group Chat R", "Group Message Subject: "
									+ msg.getSubject());
							Log.i("Group Chat R", "Group Message Subject: "
									+ msg.getFrom());
							if (!msg.getBody().equals("nullnullnullnull")) {
								messages.add(parseMessage(msg));
								setListAdapter();
							}
						}

					});
				}
			}

		});

	}

	private void setSend() {
		Button send = (Button) this.findViewById(R.id.groupsendBtn);
		send.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = msg.getText().toString();
				msg.setText("");
				if (!text.equals("")) {
					Log.i("GroupChat", "Sending group message: " + text);
					try {
						Message msg = muc.createMessage();
						msg.setBody(text);
						msg.setSubject(useremail);
						muc.sendMessage(msg);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	public void sentMessageUpdate(Message msg) {
		String save = parseMessage(msg);
		if (save != null)
			if (!save.equals("")) {
				messages.add(parseMessage(msg));

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setListAdapter();
					}
				});
			}
	}

	protected void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.listitem, messages);
		msglist.setAdapter(adapter);
		msglist.invalidate();
		if (msglist.getAdapter().getCount() > 0)
			msglist.setSelection(msglist.getAdapter().getCount() - 1);
	}

	private String parseMessage(Message msg) {
		String res = "";
		if (lastmsg != null) {
			Log.d("Debug Receive", "Pass lastmsg != null");
			if (lastmsg.getSubject() != null && msg.getSubject() != null) {
				Log.d("Debug Receive", "Pass subject != null");
				if (!lastmsg.getSubject().equals(msg.getSubject())) {
					// res = msg.getSubject() + ":\n\t" + msg.getBody();
					res = "\t" + msg.getBody();
				} else {
					res = "\t" + msg.getBody();
				}
			} else if (lastmsg.getSubject() == null || msg.getSubject() == null)
				// res = msg.getSubject() + ":\n\t" + msg.getBody();
				res = "\t" + msg.getBody();

		} else {
			// res = msg.getSubject() + ":\n\t" + msg.getBody();
			res = "\t" + msg.getBody();
		}
		this.lastmsg = msg;
		return res;
	}

	@Override
	protected void onDestroy() {
//		GroupChatController.muc.leave();
//		GroupChatController.muc = null;
//		muc.leave();
//		muc = null;
		super.onDestroy();
	}

	public void receivedMessageUpdate(Message msg) {
		if (this.activity_active) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							"New message from buddy", Toast.LENGTH_LONG).show();
				}
			});
		}
	}

}
