package com.cs110.mycity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BuddyList extends ListActivity {


	ArrayList<String> userList = new ArrayList<String>();
	BuddyListArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddylist);

		XMPPConnection connection = XMPPLogic.getInstance().getConnection();

		adapter = new BuddyListArrayAdapter(this, userList);
		ListView lv = getListView();

		// assign adapter to listview
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(view.getContext(), BuddyChat.class);
				
				
				i.putExtra("SELECTED_BUDDY", userList.get(position).substring(1));
				startActivity(i);
			
			
			}
		}); 

		getBuddies(connection);


	}

	private void getBuddies(XMPPConnection connection) {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			String user = new String();
			
			// get the Presence of the user
			Presence entryPresence = roster.getPresence(entry.getUser());
			Presence.Type type = entryPresence.getType();
			if (type == Presence.Type.available)
				user = "A";
			else {
				user = "U";
			}
			
			user += entry.getUser();  
			userList.add(user);


//						   Log.d("XMPPChatDemoActivity",  "--------------------------------------");
//						   Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
//						   Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
//						   Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
//						   Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
//						   Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
//						

						
			
		}
		
		// sort by avaliablility
		Collections.sort(userList);
		
		adapter.notifyDataSetChanged();

	}

	


}

