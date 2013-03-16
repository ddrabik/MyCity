package com.cs110.mycity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class MapHelper {

	private static final String TAG = "MAPHELPER";

	private static final double THRESHOLD = 0.05;
	
	private static MapHelper mInstance = null;

	private static HashMap<String, Location> buddyLocations = new HashMap<String, Location>();
	
	private static MapService chatService = MapService.getInstance();

	public synchronized static MapHelper getInstance() {
		if(mInstance==null){
			mInstance = new MapHelper();
		}
		return mInstance;
	}

	private MapHelper() {

	}
	
	
	/**
	 * @param buddy, location
	 * @precondition: location must not be null
	 * @precondition: buddy must be a valid email address
	 * call to send messages
	 */
	public void sendMessageTo(String buddy, Location location) {
		assert buddy.contains("@") && buddy.contains(".");
		assert location != null;
		   
		Log.d("MAPHELPER", "Sending message...");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
		
		
		 //get current date time with Date()
		   Date date = new Date();
		   System.out.println(dateFormat.format(date));
		   String date1 = dateFormat.format(date);
		   String date2 = dateFormat2.format(date);
		   String dateFinal = date1 + 'T' + date2 + 'Z';
		   System.out.println(dateFinal);
	 
		   //get current date time with Calendar()
		   Calendar cal = Calendar.getInstance();
		   System.out.println(dateFormat.format(cal.getTime()));
		   
		   double lat = location.getLatitude();
		   double lon = location.getLongitude();
		   
		   int ele = 0;
		   String time = dateFinal;

		  
		   String xml =  String.format(Locale.US, "<trkpt lat=\"%f\" lon=\"%f\">\n<ele>0</ele><time>%s</time>\n</trkpt>", lat, lon, ele, time);
			Log.d("MAPHELPER", "Sending " + lat + "," + lon + " to.." + buddy );
			Log.d("MAPHELPER", "To:" + buddy + " -- " + xml);
		   chatService.sendMessageTo(buddy, xml);
	}
	
	/**
	 * @param location
	 * @precondition: location must not be null
	 */
	public void sendToAllBuddies(Location location){ //put on hold
		assert location != null;
		assert chatService.getBuddyList() != null;
		
		Log.d("MAPHELPER", "Sending to all...");

		
//		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();
//		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> allBuddies = chatService.getBuddyList();

		
		for(String s: allBuddies){
			sendMessageTo(s, location);
			
		}

		
	}
	
	
	/**
	 * @param location
	 * @precondition: location must not be null
	 */
	public void sendToAllUsers(Location location){
		assert location != null;
		Log.d("MAPHELPER", "Sending to all...");

		
		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();

		
		while(it.hasNext()) {

			Map.Entry<String, Location> pairs = it.next();
			sendMessageTo(pairs.getKey(), (pairs.getValue()));
			Log.d("MAPHELPER", "Sending to.." + pairs.getKey());

		}
		Log.d("MAPHELPER", "Finished iterating...");

		
	}
	
	
	
	
	/**
	 * @param buddy: email address of the buddy that sent the message  
	 * @param xml: information sent through an xml string which includes location and 
	 * @precondition buddy must be a valid email address
	 * @precondition xml must be in correct format 
	 * 					ex: 	<trkpt lat="46.57608333" lon="8.89241667">
	 *							<ele>2376</ele><time>2007-10-14T10:09:57Z</time>
	 *							</trkpt>
	 */
	public void receivedLocationFrom(String buddy, String xml){
		assert buddy != null && xml != null;
		assert buddy.contains("@") && buddy.contains(".");
		assert xml.endsWith("</trkpt>");
		Log.d("MAPHELPER", "Receiving locations. ..");

		//parse xml here, turn into a location, put into hashmap.
		String lats = xml.substring(xml.indexOf("lat") + 5,xml.indexOf("lon") - 2 );
		String lons = xml.substring(xml.indexOf("lon") + 5,xml.indexOf('>') - 1 );
		
		Log.d("MAPHELPER", "  THE LON IS " + lons);
		Log.d("MAPHELPER", "  THE LAT IS " + lats);
		
		double lat = Double.parseDouble(lats);
		double lon = Double.parseDouble(lons);
		Location buddyLoc = new Location("");
		
		buddyLoc.setLatitude(lat);
		buddyLoc.setLongitude(lon);
		
		
		//check if we have seen buddy before
		if(buddyLocations.containsKey(buddy)){
			Log.d("MAPHELPER", "buddy moved location!");
			//if buddy moved, delete pin and redraw
			if(	 didBuddyMove(buddyLoc, buddyLocations.get(buddy))  ){
				buddyLocations.remove(buddy);
			}
		}
		
		buddyLocations.put(buddy, buddyLoc);
		Location currentLocation = MappingActivity.getLocationStatic();
		sendMessageTo(buddy, currentLocation);
		
		MappingActivity mapAct = MappingActivity.getInstance();
		mapAct.drawCurrPositionOverlay();
		mapAct.drawBuddies();
		
		
		Log.d("MAPHELPER", "ADDED LOCATION: " + lat + ',' + lon + "   " + buddy );
		System.out.println("ADDED LOCATION: " + lat + ',' + lon + "   " + buddy);
		
		
	}
	
	
	private boolean didBuddyMove(Location l1, Location l2){
		return l1.getLatitude() != l2.getLatitude() || (l1.getLongitude() != l2.getLongitude()) ;
	}
	
	/**
	 * @return HashMap where key = buddy's email and value = buddy's location
	 * @ensure does not return null
	 */
	public HashMap<String, Location> getBuddyLocations(){
		assert buddyLocations != null;
		return buddyLocations;
	}
	

}
