package com.cs110.mycity.Chat;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cs110.mycity.R;

public class BuddyView extends ListActivity implements Observer{
	public static final String TAG = "BUDDYVIEW";
	private Handler mHandler = new Handler();
	private SocketListener mService = null;
	private ArrayList<String> buddyList = new ArrayList<String>();
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buddylist);
		
		listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(buddyList.get(position) != null) {
					Intent i;
					if(buddyList.get(position).startsWith("2")) {
						i = new Intent(view.getContext(), GroupChatView.class);
						i.putExtra("ROOM_MEMBERS", buddyList.get(position).substring(1));
					} else {
						i = new Intent(view.getContext(), ChatView.class);
						i.putExtra("SELECTED_BUDDY", buddyList.get(position).substring(1));
					}
					startActivity(i);
				}
				
			}
		}); 
		
	}
	
	
	private ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			Log.d(TAG, "Connected to service.");
			mService = ((SocketListener.LocalBinder) binder).getService();
			mService.registerObserver(BuddyView.this);
			updateView();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Disconnected from service.");
			mService = null;
		}
	};
	
	private void updateView() {
		Log.d(TAG, "Updating View");
		if(mService != null) {
			buddyList = mService.getBuddyList();
		}
		BuddyListArrayAdapter adapter = new BuddyListArrayAdapter(this, buddyList);
		listView.setAdapter(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, SocketListener.class), mConn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mConn);
	}

	@Override
	public void update(Subject s) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				updateView();
			}
		});	
	}
}
