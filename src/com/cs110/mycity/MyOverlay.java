/**
 * 
 */
package com.cs110.mycity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.cs110.mycity.Chat.BuddyView;
import com.cs110.mycity.Chat.ChatView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

/**
 * TODO (zjivani): write class javadoc
 *
 * @author zjivani
 *
 */
public class MyOverlay extends BalloonItemizedOverlay<OverlayItem>{

	private int type;
	private Context mContext;
	private ArrayList<OverlayItem> buddies = new ArrayList<OverlayItem>();
	private Location currentLocation;
	private MapActivity mapAct;

	public MyOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker),mapView);
		boundCenter(defaultMarker);
		mContext = mapView.getContext();
	}
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return buddies.get(i);
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return buddies.size();
	}
	public void addOverlay(OverlayItem overlay) {
		buddies.add(overlay);
		populate();
	}
	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		int type = this.getType();
		switch(type){
		case 1: {
			Toast.makeText(mContext, "Self Selected " + type,
					Toast.LENGTH_LONG).show();
			
			
			
			return true;
		}
		case 2: {
			Toast.makeText(mContext, "Buddy Selected " + type,
					Toast.LENGTH_LONG).show();
			
			Intent i = new Intent();
			i.setClass(mContext, ChatView.class);
			i.putExtra("SELECTED_BUDDY", item.getTitle());
            mContext.startActivity(i);
	
			return true;

		}
		case 3: {
//			Toast.makeText(mContext, "PoI Selected " + type,
//					Toast.LENGTH_LONG).show();
			return true;

		}
		case 4: {
			Toast.makeText(mContext, "User Content Selected " + type,
					Toast.LENGTH_LONG).show();
			
			Intent i = new Intent();
			i.setClass(mContext, UserContent.class);
//			i.putExtra("SELECTED_BUDDY", item.getTitle());
            mContext.startActivity(i);
			
			
			return true;
		}
		}
		return false;
	}
	
	
	
	public void setCurrentLocation(Location loc){
		this.currentLocation = loc;
	}
	
	public void clear(){
		buddies.clear();
	}
	
	public int getType(){
		return this.type;
	}
	public void setType(int i) {
		// TODO Auto-generated method stub
		
		if(i>4 || i<1){
			return;
		}
		this.type = i;
		
		
	}
	
	

}
