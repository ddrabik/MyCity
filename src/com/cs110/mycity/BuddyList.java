package com.cs110.mycity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BuddyList extends ListActivity {

	private static final String TAG = "BUDDYLIST";
	BuddyListArrayAdapter adapter;
	ChatHelper chatHelper = ChatHelper.getInstance();
	ArrayList<String> buddyList;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddylist);
		
		lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(view.getContext(), BuddyChat.class);
				if(buddyList.get(position) != null) {
					i.putExtra("SELECTED_BUDDY", buddyList.get(position).substring(1));
				}
				startActivity(i);
			}
		}); 
	}
	
	private void updateView() {
		Log.d(TAG, "Updating View");
		buddyList = chatHelper.getBuddyList();
		adapter = new BuddyListArrayAdapter(this, buddyList);
		lv.setAdapter(adapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateView();
	}
}

