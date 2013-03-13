package com.cs110.mycity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.cs110.mycity.Chat.BuddyView;
import com.cs110.mycity.Chat.ChatView;
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
	private static Location helperLocation = null;
	private Button btnUpdate;
	private MyOverlay currPos= null;

	private HashMap<String, Location> buddyLocations;

	private static MappingActivity mInstance = null;


	private LocationBroadCaster locBroad = null;
	private MapHelper mapHelper = MapHelper.getInstance();

	private static String buddyName;


	//	private MappingActivity(){
	//		
	//	}


	public Location getCurrentLocation() {
		helperLocation = this.currentLocation;
		return this.currentLocation;

	}


	public static Location getLocationStatic() {
		return helperLocation;
	}

	public GeoPoint getCurrentPoint() {
		return this.currentPoint;
	}

	public MyOverlay getCurrPos() {
		return this.currPos;
	}








	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInstance = MappingActivity.this;

		setContentView(R.layout.activity_mapping);
		mapView = (MapView)findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(15);
		getLastLocation();
		drawCurrPositionOverlay();
		animateToCurrentLocation();
		LoadPlaces lp = new LoadPlaces();
		lp.execute();


		btnUpdate = (Button) findViewById(R.id.chatView_button);
		btnUpdate.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), BuddyView.class);
				startActivity(i);
			}
		});







		//new thread to run in background that shouts locations and waits for response?
		int delay = 5*10000; // delay for 1 sec. 
		int period = 100 * 10000; // repeat every 10 sec. 
		Timer timer = new Timer(); 

		timer.scheduleAtFixedRate(new TimerTask() 
		{ 
			@Override
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
			helperLocation = this.currentLocation;

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
		helperLocation = this.currentLocation;

		drawCurrPositionOverlay();
		LoadPlaces lp = new LoadPlaces();
		lp.execute();
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		setCurrentLocation(newLocation);
		animateToCurrentLocation();


		Log.d("MAPACTIVITY", "LOCATION HAS CHANGEDD >>>>>>>>>>>>>>>>>>");
		locBroad = new LocationBroadCaster();
		locBroad.execute((Void) null);
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

	public  void drawCurrPositionOverlay(){


		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		Drawable mymarker = getResources().getDrawable(R.drawable.mylocation);

		currPos = null;
		currPos = new MyOverlay(mymarker,mapView);

		if(currentPoint!=null){
			OverlayItem overlayitem = new OverlayItem(currentPoint, "Me", "Here I am!");

			currPos.addOverlay(overlayitem);
			overlays.add(currPos);
			currPos.setCurrentLocation(currentLocation);
		}


		HashMap<String, Location> buddyLocations = mapHelper.getBuddyLocations();
		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();



		while(it.hasNext()){
			Log.d("MAPACTIVITY","DRAWING PINS");
			Drawable buddymarker = getResources().getDrawable(R.drawable.buddy);
			MyOverlay buddyPin = new MyOverlay(buddymarker, mapView);
			//			overlays.remove(buddyPin);
			Map.Entry<String, Location> pairs = it.next();
			if(pairs.getValue() != null){
				GeoPoint point = new GeoPoint( (int) (pairs.getValue().getLatitude()*1E6),  (int) (pairs.getValue().getLongitude()*1E6));


				if(point!=null){
					Log.d("MAPACTIVITY", "GEOPOINT IS: " + point.toString());

					OverlayItem overlayitem2 = new OverlayItem(point, pairs.getKey(), "Here I am!");

					buddyName = pairs.getKey();
					buddyPin.addOverlay(overlayitem2);	


					//set the chat button to chat with map buddies
					Button overlayChatButton =(Button) findViewById(R.id.overlay_chat_button);
					overlayChatButton.setOnClickListener(new View.OnClickListener() {   
						@Override
						public void onClick(View v) {
					
							Log.d("OVERLAY", "chatting attempt.......");
						
							Intent i = new Intent(v.getContext(), ChatView.class);
							if(buddyName != null) {
								i.putExtra("SELECTED_BUDDY", buddyName.substring(1));
							}
							startActivity(i);
						} 
					});

					

					overlays.add(buddyPin);
					buddyPin.setCurrentLocation(pairs.getValue());
					LoadPlaces lp = new LoadPlaces();
					lp.execute();

				}
			}
		}

	}

	public void onOverlayClick(View v){
		Log.d("OVERLAY", "chatting attempt.......");
		
		//Andy: test buddyName. I think it might just be the last buddy added to the map.
		//i don't know where we get and where we can store the proper value
		Intent i = new Intent(v.getContext(), ChatView.class);
		if(buddyName != null) {
			i.putExtra("SELECTED_BUDDY", buddyName);
		}
		startActivity(i);
	}








	public synchronized static MappingActivity getInstance() {
		if(mInstance==null){
			mInstance = new MappingActivity();
		}
		return mInstance;
	}


	public void drawPOIs(PlaceList nearPlaces) {
		Drawable marker = getResources().getDrawable(R.drawable.city_icon);
		MyOverlay pOIs = new MyOverlay(marker, mapView);
		List overlays = mapView.getOverlays();

		// loop through each place
		for (Place p : nearPlaces.results) {
			int lat = (int) (p.geometry.location.lat * 1e6);
			int lng = (int) (p.geometry.location.lng * 1e6);

			OverlayItem overlayItem = new OverlayItem(new GeoPoint(lat, lng),
					p.name, p.vicinity);

			pOIs.addOverlay(overlayItem);
			Button overlayChatButton =(Button) findViewById(R.id.overlay_chat_button);
//			overlayChatButton.setVisibility(0);
//			overlayChatButton.setEnabled(false);
			
			Log.d("POI", "Place: " + p.name+" "+lat+ " "+lng);
		}

		overlays.add(pOIs);
		pOIs.setCurrentLocation(currentLocation);
	}


	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask <String, String, PlaceList > {
		public GooglePlaces googlePlaces;
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/**
		 * getting Places JSON
		 * */
		protected PlaceList doInBackground(String... args) {
			// creating Places class object

			googlePlaces = new GooglePlaces();
			PlaceList nearPlaces = null;
			try {
				// Separate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				//
				String types = null; // Listing all places

				// Radius in meters - increase this value if you don't find any
				// places
				double radius = 1500; // 1500 meters

				if (currentLocation != null) {
					// get nearest places
					nearPlaces = googlePlaces.search(
							currentLocation.getLatitude(),
							currentLocation.getLongitude(), radius, types);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return nearPlaces;
		}

		/**
		 * After completing background task show the data in UI Always use
		 * runOnUiThread(new Runnable()) to update UI from background thread,
		 * otherwise you will get error
		 * **/
		protected void onPostExecute(PlaceList nearPlaces) {
			//Log error
			if(nearPlaces == null )
				return;

			/**
			 * Updating parsed Places into LISTVIEW
			 * */
			// Get json response status
			String status = nearPlaces.status;

			// Check for all possible status
			if (status.equals("OK")) {
				// Successfully got places details
				if (nearPlaces.results != null) {
					drawPOIs(nearPlaces);
				}
			} else if (status.equals("ZERO_RESULTS")) {
				// Zero results found
				Toast.makeText(
						MappingActivity.this,
						"Sorry no places found. Try to change the types of places",
						Toast.LENGTH_LONG).show();
			} else if (status.equals("UNKNOWN_ERROR")) {
				Toast.makeText(MappingActivity.this,
						"Sorry unknown error occured.",
						Toast.LENGTH_LONG).show();
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				Toast.makeText(
						MappingActivity.this,
						"Sorry query limit to google places is reached",
						Toast.LENGTH_LONG).show();
			} else if (status.equals("REQUEST_DENIED")) {
				Toast.makeText(MappingActivity.this,
						"Sorry error occured. Request is denied",
						Toast.LENGTH_LONG).show();
			} else if (status.equals("INVALID_REQUEST")) {
				Toast.makeText(MappingActivity.this,
						"Sorry error occured. Invalid Request",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MappingActivity.this,
						"Sorry error occured.", Toast.LENGTH_LONG)
						.show();
			}
		}

	}


}



