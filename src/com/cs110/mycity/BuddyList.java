package com.cs110.mycity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

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


	ArrayList<String> listItems = new ArrayList<String>();
	BuddyListArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddylist);

		XMPPConnection connection = XMPPLogic.getInstance().getConnection();

		adapter = new BuddyListArrayAdapter(this, listItems);
		ListView lv = getListView();

		// assign adapter to listview
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(view.getContext(), BuddyChat.class);
				
				
				i.putExtra("SELECTED_BUDDY", listItems.get(position).substring(1));
				startActivity(i);
			
			
			}
		}); 

		getBuddies(connection);


	}

	private void getBuddies(XMPPConnection connection) {
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			String str = new String();
			
			// get the Presence of the user
			Presence entryPresence = roster.getPresence(entry.getUser());
			Presence.Type type = entryPresence.getType();
			if (type == Presence.Type.available)
				str = "A";
			else {
				str = "U";
			}
			
			if( entry.getName() != null ) {
				str += entry.getName().toLowerCase(Locale.US);
			} else {
				str += entry.getUser();
			}  
			
			listItems.add(str);


			//			   Log.d("XMPPChatDemoActivity",  "--------------------------------------");
			//			   Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
			//			   Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
			//			   Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
			//			   Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
			//			   Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
			//			   Presence entryPresence = roster.getPresence(entry.getUser());
			//			
			//			   Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
			//			   Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
			//			
			//			   Presence.Type type = entryPresence.getType();
			//			   if (type == Presence.Type.available)
			//			 	  Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
			//			   Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
		}
		
		// sort by avaliablility
		Collections.sort(listItems);
		
		adapter.notifyDataSetChanged();

	}

	


}

