package com.cs110.mycity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs110.mycity.MainActivity.UserLoginTask;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MappingActivity extends MapActivity implements LocationListener {

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private GeoPoint currentPoint;
	private Location currentLocation = null;
	private Button btnUpdate;
	private MyOverlay currPos= null;

	private HashMap<String, Location> buddyLocations;
	

	public Location getCurrentLocation() {
		return this.currentLocation;
	}
	
	public GeoPoint getCurrentPoint() {
		return this.currentPoint;
	}
	
	public MyOverlay getCurrPos() {
		return this.currPos;
	}
	


	private LocationBroadCaster locBroad = null;

	private MapHelper mapHelper = MapHelper.getInstance();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mapping);
		mapView = (MapView)findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(15);
		getLastLocation();
		drawCurrPositionOverlay();
		animateToCurrentLocation();

		btnUpdate = (Button) findViewById(R.id.chatView_button);
		btnUpdate.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), BuddyList.class);
				startActivity(i);
			}
		});


		//new thread to run in background that shouts locations and waits for response?
		int delay = 8*10000; // delay for 1 sec. 
		int period = 25 * 10000; // repeat every 10 sec. 
		Timer timer = new Timer(); 

		timer.scheduleAtFixedRate(new TimerTask() 
		{ 
			public void run() 
			{ 
				Log.d("MAPHELPER", "LOOPING AGAIN");
				locBroad = new LocationBroadCaster();
				locBroad.execute((Void) null);

				Log.d("MAP", "finished background thread for buddies");
			}
		}, delay, period);  


	}


	public class LocationBroadCaster extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.


			try {
				//acquire buddy locations.
				//have chat service send a message to all buddies
				mapHelper.sendToAllBuddies(currentLocation);

				//map helper returns a hashmap with buddies and their locations
				buddyLocations = mapHelper.getBuddyLocations();

				return true;
			}
			catch(NullPointerException e){

				return false;

			}





		}

		@Override
		protected void onPostExecute(final Boolean success) {


			if (success) {
				//log that a shout has happened with echo's successfuly? update here.
				//update buddy locations.	
				//remove all current overlay items,
				//iterate through hashmap and redo overlay items

			} else {

			}
		}

		@Override
		protected void onCancelled() {
			locBroad = null;
		}
	}








	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mapping, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void getLastLocation(){
		String provider = getBestProvider();
		if(provider != null) {
			currentLocation = locationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, "Please enable your location",  Toast.LENGTH_LONG).show();
		}

		if(currentLocation != null) {
			setCurrentLocation(currentLocation);
		} else {
			Toast.makeText(this, "Location not yet acquired", Toast.LENGTH_LONG).show();
		}
	}



	public void animateToCurrentLocation(){
		if(currentPoint!=null){
			mapController.animateTo(currentPoint);
		}
	}
	public String getBestProvider(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		return bestProvider;
	}
	public void setCurrentLocation(Location location){
		int currLatitude = (int) (location.getLatitude()*1E6);
		int currLongitude = (int) (location.getLongitude()*1E6);
		currentPoint = new GeoPoint(currLatitude,currLongitude);
		currentLocation = new Location("");
		currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
		currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
		drawCurrPositionOverlay();
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		setCurrentLocation(newLocation);
		animateToCurrentLocation();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void onResume() {
		super.onResume();
		String provider = getBestProvider();
		if(provider == null) {
			Toast.makeText(this, "Please enable your location",  Toast.LENGTH_LONG).show();
		} else {
			locationManager.requestLocationUpdates(provider, 1000, 1, this);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	public void drawCurrPositionOverlay(){
		List<Overlay> overlays = mapView.getOverlays();
		overlays.remove(currPos);
		Drawable marker = getResources().getDrawable(R.drawable.mylocation);
		currPos = new MyOverlay(marker,mapView);
		if(currentPoint!=null){
			OverlayItem overlayitem = new OverlayItem(currentPoint, "Me", "Here I am!");
			currPos.addOverlay(overlayitem);
			overlays.add(currPos);
			currPos.setCurrentLocation(currentLocation);
		}
	}







}
