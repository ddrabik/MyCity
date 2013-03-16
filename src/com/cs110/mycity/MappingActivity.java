package com.cs110.mycity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cs110.mycity.MainActivity.UserLoginTask;
import com.cs110.mycity.MapContentView;
import com.cs110.mycity.Chat.BuddyView;
import com.cs110.mycity.Chat.ChatView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.readystatesoftware.*;

//import com.cs110.mycity.R;

public class MappingActivity extends MapActivity implements LocationListener {

	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private GeoPoint currentPoint;
	private Location currentLocation = null;
	private static Location helperLocation = null;
	private Button btnUpdate;
	private MyOverlay currPos = null;
	private MyOverlay buddyPin = null;

	private HashMap<String, Location> buddyLocations;
	private Set<String> localBuddies;

	private static MappingActivity mInstance = null;

	private LocationBroadCaster locBroad = null;
	private MapHelper mapHelper = MapHelper.getInstance();

	private static String buddyName;
	private double radius = 1.5; // km
	
	private NotificationManager bNotificationManager;
	private int notifCount;
	private String notifTag = "MAPACTIVITY";

	// variable for determining long press and then automatically adding a pin
	// to the map
	private int minMillisecondThresholdForLongClick = 800;
	private long startTimeForLongClick = 0;
	private float xScreenCoordinateForLongClick;
	private float yScreenCoordinateForLongClick;
	private float xtolerance = 10;// x pixels that your finger can be off but
									// still constitute a long press
	private float ytolerance = 10;// y pixels that your finger can be off but
									// still constitute a long press
	private float xlow; // actual screen coordinate when you subtract the
						// tolerance
	private float xhigh; // actual screen coordinate when you add the tolerance
	private float ylow; // actual screen coordinate when you subtract the
						// tolerance
	private float yhigh; // actual screen coordinate when you add the tolerance

	// private MappingActivity(){
	//
	// }

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
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(15);
		getLastLocation();
		drawCurrPositionOverlay();
		drawBuddies();
		animateToCurrentLocation();
		
		bNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		btnUpdate = (Button) findViewById(R.id.chatView_button);
		btnUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), BuddyView.class);
				startActivity(i);
			}
		});

		// new thread to run in background that shouts locations and waits for
		// response?
		int delay = 1000;// 5*10000; // delay for 1 sec.
		int period = 10000;// 100 * 10000; // repeat every 10 sec.
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
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
				// acquire buddy locations.
				// have chat service send a message to all buddies
				mapHelper.sendToAllBuddies(currentLocation);

				// map helper returns a hashmap with buddies and their locations
				buddyLocations = mapHelper.getBuddyLocations();

				return true;
			} catch (NullPointerException e) {

				return false;

			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				// log that a shout has happened with echo's successfuly? update
				// here.
				// update buddy locations.
				// remove all current overlay items,
				// iterate through hashmap and redo overlay items

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

	public void getLastLocation() {
		String provider = getBestProvider();
		if (provider != null) {
			currentLocation = locationManager.getLastKnownLocation(provider);
			helperLocation = this.currentLocation;

		} else {
			Toast.makeText(this, "Please enable your location",
					Toast.LENGTH_LONG).show();
		}

		if (currentLocation != null) {
			setCurrentLocation(currentLocation);
		} else {
			Toast.makeText(this, "Location not yet acquired", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void animateToCurrentLocation() {
		if (currentPoint != null) {
			mapController.animateTo(currentPoint);
		}
	}

	public String getBestProvider() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		return bestProvider;
	}

	public void setCurrentLocation(Location location) {
		int currLatitude = (int) (location.getLatitude() * 1E6);
		int currLongitude = (int) (location.getLongitude() * 1E6);
		currentPoint = new GeoPoint(currLatitude, currLongitude);
		currentLocation = new Location("");
		currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
		currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
		helperLocation = this.currentLocation;

		drawCurrPositionOverlay();
		drawBuddies();
		new LoadPlaces().execute();
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
		if (provider == null) {
			Toast.makeText(this, "Please enable your location",
					Toast.LENGTH_LONG).show();
		} else {
			locationManager.requestLocationUpdates(provider, 1000, 1, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	public void drawCurrPositionOverlay() {

		List<Overlay> overlays = mapView.getOverlays();
		// overlays.clear();
		overlays.remove(currPos);
		Drawable mymarker = getResources().getDrawable(R.drawable.mylocation);

		currPos = new MyOverlay(mymarker, mapView);
		currPos.setType(1);

		if (currentPoint != null) {
			OverlayItem overlayitem = new OverlayItem(currentPoint, "Me",
					"Here I am!");

			currPos.addOverlay(overlayitem);
			overlays.add(currPos);
			currPos.setCurrentLocation(currentLocation);
		}

	}

	@SuppressLint("NewApi")
	public void drawBuddies() {

		List<Overlay> overlays = mapView.getOverlays();
		buddyLocations = mapHelper.getBuddyLocations();
		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();
		Log.d("MAPACTIVITY", "DRAWING PINS");
		Drawable buddymarker = getResources().getDrawable(R.drawable.buddy);
		overlays.remove(buddyPin);
		if (buddyPin == null)
			buddyPin = new MyOverlay(buddymarker, mapView);
		buddyPin.setType(2);
		buddyPin.clear();

		while (it.hasNext()) {
			Map.Entry<String, Location> pairs = it.next();
			if (pairs.getValue() != null) {
				GeoPoint point = new GeoPoint((int) (pairs.getValue().getLatitude() * 1E6), (int) (pairs.getValue().getLongitude() * 1E6));

				if (point != null) {
					Log.d("MAPACTIVITY", "GEOPOINT IS: " + point.toString());

					OverlayItem overlayitem2 = new OverlayItem(point, pairs.getKey(), "Here I am!");

					buddyName = pairs.getKey();
					buddyPin.addOverlay(overlayitem2);

					Log.d("MAPACTIVITY", "buddyName = " + buddyName);

					// check if location is within radius
					double earthR = 6371; // km
					double latDiff = Math.toRadians(currentLocation.getLatitude() - pairs.getValue().getLatitude());
					double longDiff = Math.toRadians(currentLocation.getLongitude() - pairs.getValue().getLongitude());
					double lat1 = Math.toRadians(currentLocation.getLatitude());
					double lat2 = Math.toRadians(pairs.getValue().getLatitude());

					double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
							+ Math.sin(longDiff / 2) * Math.sin(longDiff / 2)
							* Math.cos(lat1) * Math.cos(lat2);
					double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
					double d = earthR * c;

					boolean isLocal = d < radius;
					// add to list of local buddies

					Log.d("MAPACTIVITY", "isLocal = " + isLocal);

					if (isLocal) {
						Log.d("MAPACTIVITY", buddyName + " is close!");
						if (localBuddies == null) {
							localBuddies = new HashSet<String>();
						}
						if (!localBuddies.contains(buddyName)) {
							Log.d("MAPACTIVITY", "NOTIFY:" + buddyName + " is close!");
							NotificationCompat.Builder bBuilder = new NotificationCompat.Builder(this)
									.setSmallIcon(R.drawable.ic_launcher)
									.setAutoCancel(true).setContentTitle("A friend is nearby!")
									.setContentText(buddyName);
							
							
//							Intent resultIntent = new Intent(this, MappingActivity.class);
//							TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//							stackBuilder.addParentStack(MappingActivity.class);
//							stackBuilder.addNextIntent(resultIntent);
//							PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//							bBuilder.setContentIntent(resultPendingIntent);
							
							bNotificationManager.notify(notifTag, notifCount++, bBuilder.build());
							localBuddies.add(buddyName);
						}
					} else {
						if (localBuddies != null) {
							localBuddies.remove(buddyName);
						}
					}
					if (localBuddies != null) {
						localBuddies.remove(buddyName);
						Log.d("MAPACTIVITY", "localBuddies.size = " + localBuddies.size());
					}
					LayoutInflater inflater = this.getLayoutInflater();
					LinearLayout v = (LinearLayout) inflater.inflate(
							R.layout.balloon_overlay, null);

//		
					Log.d("MAPACTIVITY", "Adding buddy pin!");

					overlays.add(buddyPin);
					buddyPin.setCurrentLocation(pairs.getValue());
					mapView.postInvalidate();

				}
			}
		}

	}

	// public void onOverlayClick(View v){
	// Log.d("OVERLAY", "chatting attempt.......");
	//
	// //Andy: test buddyName. I think it might just be the last buddy added to
	// the map.
	// //i don't know where we get and where we can store the proper value
	// Intent i = new Intent(v.getContext(), ChatView.class);
	// if(buddyName != null) {
	// i.putExtra("SELECTED_BUDDY", buddyName);
	// }
	// startActivity(i);
	// }

	public synchronized static MappingActivity getInstance() {
		if (mInstance == null) {
			mInstance = new MappingActivity();
		}
		return mInstance;
	}

	public void drawPOIs(PlaceList nearPlaces) {
		Drawable marker = getResources().getDrawable(R.drawable.city_icon);
		MyOverlay pOIs = new MyOverlay(marker, mapView);
		pOIs.setType(3);
		List overlays = mapView.getOverlays();

		// loop through each place
		for (Place p : nearPlaces.results) {
			int lat = (int) (p.geometry.location.lat * 1e6);
			int lng = (int) (p.geometry.location.lng * 1e6);

			try {
				PlaceList pl = new GooglePlaces().details(p.reference);
				OverlayItem overlayItem = new OverlayItem(
						new GeoPoint(lat, lng), p.name, "Address: \n"
								+ pl.result.formatted_address + "\n"
								+ "Phone number: "
								+ pl.result.formatted_phone_number + "\n"
								+ "Website: " + pl.result.website);

				pOIs.addOverlay(overlayItem);
				
				
				

				Log.d("POI", "Place: " + p.name + " " + lat + " " + lng);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		overlays.add(pOIs);
		pOIs.setCurrentLocation(currentLocation);
		mapView.postInvalidate();
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, PlaceList> {
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
			// Log error
			if (nearPlaces == null)
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
						"Sorry unknown error occured.", Toast.LENGTH_LONG)
						.show();
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				Toast.makeText(MappingActivity.this,
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
				Toast.makeText(MappingActivity.this, "Sorry error occured.",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	// public void addUserContent(GeoPoint point) {
	// Drawable marker = getResources().getDrawable(R.drawable.usercontent);
	// List<Overlay> overlays = mapView.getOverlays();
	// MyOverlay usercontent = new MyOverlay(marker,mapView);
	// OverlayItem overlayitem = new OverlayItem(point, "New User Content",
	// "Add a description");
	// usercontent.addOverlay(overlayitem);
	// overlays.add(usercontent);
	// Location l = new Location("");
	// l.setLatitude((point.getLatitudeE6() * 1e6));
	// l.setLongitude(point.getLongitudeE6() * 1e6);
	// usercontent.setCurrentLocation(l);
	// //mapView.postInvalidate();
	// }
	//
	//
	// class OnLongPressListener implements MapContentView.OnLongpressListener {
	//
	// @Override
	// public void onLongpress(MapView view, GeoPoint longpressLocation) {
	//
	// final GeoPoint gp = longpressLocation;
	//
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// MappingActivity.this.addUserContent(gp);
	// }
	//
	// });
	//
	// }
	//
	// }

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		/*
		 * We want to capture the place the user long pressed on the map and add
		 * a marker (pin) on the map at that lat/long. This solution: 1. Allows
		 * you to set the time threshold for what constitutes a long press 2.
		 * Doesn't get fooled by scrolling, multi-touch, or non-multi-touch
		 * events
		 * 
		 * Thank you Roger Kind Kristiansen for the main idea
		 */

		// get the action from the MotionEvent: down, move, or up
		int actionType = ev.getAction();

		if (actionType == MotionEvent.ACTION_DOWN) {
			// user pressed the button down so let's initialize the main
			// variables that we care about:
			// later on when the "Action Up" event fires, the "DownTime" should
			// match the "startTimeForLongClick" that we set here
			// the coordinate on the screen should not change much during the
			// long press
			startTimeForLongClick = ev.getEventTime();
			xScreenCoordinateForLongClick = ev.getX();
			yScreenCoordinateForLongClick = ev.getY();

		} else if (actionType == MotionEvent.ACTION_MOVE) {
			// For non-long press actions, the move action can happen a lot
			// between ACTION_DOWN and ACTION_UP
			if (ev.getPointerCount() > 1) {
				// easiest way to detect a multi-touch even is if the pointer
				// count is greater than 1
				// next thing to look at is if the x and y coordinates of the
				// person's finger change.
				startTimeForLongClick = 0; // instead of a timer, just reset
											// this class variable and in our
											// ACTION_UP event, the DownTime
											// value will not match and so we
											// can reset.
			} else {
				// I know that I am getting to the same action as above,
				// startTimeForLongClick=0, but I want the processor
				// to quickly skip over this step if it detects the pointer
				// count > 1 above
				float xmove = ev.getX(); // where is their finger now?
				float ymove = ev.getY();
				// these next four values allow you set a tiny box around their
				// finger in case
				// they don't perfectly keep their finger still on a long click.
				xlow = xScreenCoordinateForLongClick - xtolerance;
				xhigh = xScreenCoordinateForLongClick + xtolerance;
				ylow = yScreenCoordinateForLongClick - ytolerance;
				yhigh = yScreenCoordinateForLongClick + ytolerance;
				if ((xmove < xlow || xmove > xhigh)
						|| (ymove < ylow || ymove > yhigh)) {
					// out of the range of an acceptable long press, reset the
					// whole process
					startTimeForLongClick = 0;
				}
			}

		} else if (actionType == MotionEvent.ACTION_UP) {
			// determine if this was a long click:
			long eventTime = ev.getEventTime();
			long downTime = ev.getDownTime(); // this value will match the
												// startTimeForLongClick
												// variable as long as we didn't
												// reset the
												// startTimeForLongClick
												// variable because we detected
												// nonsense that invalidated a
												// long press in the ACTION_MOVE
												// block

			// make sure the start time for the original "down event" is the
			// same as this event's "downTime"
			if (startTimeForLongClick == downTime) {
				// see if the event time minus the start time is within the
				// threshold
				if ((eventTime - startTimeForLongClick) > minMillisecondThresholdForLongClick) {
					// make sure we are at the same spot where we started the
					// long click
					float xup = ev.getX();
					float yup = ev.getY();
					// I don't want the overhead of a function call:
					xlow = xScreenCoordinateForLongClick - xtolerance;
					xhigh = xScreenCoordinateForLongClick + xtolerance;
					ylow = yScreenCoordinateForLongClick - ytolerance;
					yhigh = yScreenCoordinateForLongClick + ytolerance;
					if ((xup > xlow && xup < xhigh)
							&& (yup > ylow && yup < yhigh)) {

						// **** safe to process your code for an actual long
						// press ****
						// comment out these next rows after you confirm in
						// logcat that the long press works
						long totaltime = eventTime - startTimeForLongClick;
						String strtotaltime = Long.toString(totaltime);
						Log.d("long press detected: ", strtotaltime);

						// Now get the latitude/longitude of where you clicked.
						// Replace all the code below if you already know how to
						// translate a screen coordinate to lat/long. I know it
						// works though.

						// *****************
						// I have my map under a tab so I have to account for
						// the tab height and the notification bar at the top of
						// the phone.
						// Maybe there are other ways so just ignore this if you
						// already know how to get the lat/long of the pixels
						// that were pressed.
						/*
						 * int TabHeightAdjustmentPixels=tabHost.getTabWidget().
						 * getChildAt(0).getLayoutParams().height; int
						 * EntireTabViewHeight = tabHost.getHeight(); Display
						 * display = getWindowManager().getDefaultDisplay(); int
						 * EntireScreenHeight = display.getHeight(); int
						 * NotificationBarHeight
						 * =EntireScreenHeight-EntireTabViewHeight;
						 */
						// the projection is mapping pixels to where you touch
						// on the screen.
						Projection proj = mapView.getProjection();
						// GeoPoint loc =
						// proj.fromPixels((int)(ev.getX(ev.getPointerCount()-1)),
						// (int)(ev.getY(ev.getPointerCount()-1)-TabHeightAdjustmentPixels-NotificationBarHeight));
						GeoPoint loc = proj.fromPixels(
								(int) (ev.getX(ev.getPointerCount() - 1)),
								(int) (ev.getY(ev.getPointerCount() - 1)));
						int longitude = loc.getLongitudeE6();
						int latitude = loc.getLatitudeE6();
						// *****************

						// **** here's where you add code to:
						// put a marker on the map, save the point to your
						// SQLite database, etc

						Drawable marker = getResources().getDrawable(
								R.drawable.usercontent);
						List<Overlay> overlays = mapView.getOverlays();
						MyOverlay usercontent = new MyOverlay(marker, mapView);
						usercontent.setType(4);
						OverlayItem overlayitem = new OverlayItem(loc,
								"New User Content", "Add a description");
						usercontent.addOverlay(overlayitem);
						overlays.add(usercontent);
						Location l = new Location("");
						l.setLatitude(latitude * 1e6);
						l.setLongitude(longitude * 1e6);
						usercontent.setCurrentLocation(l);
						mapView.postInvalidate();

					}
				}
			}

		}

		return super.dispatchTouchEvent(ev);
	}

}
