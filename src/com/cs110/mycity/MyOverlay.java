/**
 * 
 */
package com.cs110.mycity;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

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


	private Context mContext;
	private ArrayList<OverlayItem> buddies = new ArrayList<OverlayItem>();
	private Location currentLocation;

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
		Toast.makeText(mContext, "Overlay Item " + index + " tapped!",
				Toast.LENGTH_LONG).show();
		return true;
	}
	public void setCurrentLocation(Location loc){
		this.currentLocation = loc;
	}

}
