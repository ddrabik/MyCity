package com.cs110.mycity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import com.google.android.maps.GeoPoint;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

/*
 * MapHelper class assists with the connection between buddy locations
 * on the map and the chat service to send the location broadcast
 * messages. 
 */

@SuppressLint("DefaultLocale")
public class MapHelper {

	private static final String TAG = "MAPHELPER";
	
	private static MapHelper mInstance = null;

	//Store the buddy locations in a HashMap
	private static HashMap<String, Location> buddyLocations = new HashMap<String, Location>();
	
	private static ChatService chatService = ChatService.getInstance();
	

	/*
	 * Return the MapHelper instance (singleton pattern)
	 */
	public synchronized static MapHelper getInstance() {
		if(mInstance==null){
			mInstance = new MapHelper();
		}
		return mInstance;
	}

	/*
	 * Private constructor generates an empty MapHelper
	 */
	private MapHelper() {

	}

	//call to send messages
	public void sendMessageTo(String buddy, Location location) {
		   
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

		  
		   //XML formatting string for <trkpt/> to notify buddies of location
		   String xml =  String.format(Locale.US, "<trkpt lat=\"%f\" lon=\"%f\">\n<ele>0</ele><time>%s</time>\n</trkpt>", lat, lon, ele, time);
			Log.d("MAPHELPER", "Sending " + lat + "," + lon + " to.." + buddy );

		   chatService.sendMessageTo(buddy, xml);
	}


	public void sendToAllBuddies(Location location){ //put on hold
		Log.d("MAPHELPER", "Sending to all...");

		
//		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();
//		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> allBuddies = chatService.getBuddyList();

		
		for(String s: allBuddies){
			sendMessageTo(s, location);
			
		}

		
		
//		while(it.hasNext()) {
//
//			Map.Entry<String, Location> pairs = it.next();
//			sendMessageTo(pairs.getKey(), (pairs.getValue()));
//			Log.d("MAPHELPER", "Sending to.." + pairs.getKey());
//
//		}
//		Log.d("MAPHELPER", "Finished iterating...");

		
	}
	
	
	/*
	 * Send locations too all registered online users (including gTalk-
	 * only buddies)
	 */
	public void sendToAllUsers(Location location){
		Log.d("MAPHELPER", "Sending to all...");

		
		Iterator<Map.Entry<String, Location>> it = buddyLocations.entrySet().iterator();

		
		while(it.hasNext()) {

			Map.Entry<String, Location> pairs = it.next();
			sendMessageTo(pairs.getKey(), (pairs.getValue()));
			Log.d("MAPHELPER", "Sending to.." + pairs.getKey());

		}
		Log.d("MAPHELPER", "Finished iterating...");

		
	}
	
	
	
	
	/*
	 * Accept locations from buddies. Locations are any messages
	 * that match a certain string expression.
	 */
	public void receivedLocationFrom(String buddy, String xml){
		
		
		Log.d("MAPHELPER", "Receiving locations2. ..");

		//parse xml here, turn into a location, put into hashmap.
		String pattern = "<trkpt lat=\"(-?[0-9].[0-9])\" lon=\"(-?[0-9].[0-9])\">.*";
		xml = xml.replaceAll(pattern, "$1,$2");

		
		Log.d("XXX", "XMLXML is: " + xml);
		
//		String first = xml.substring(0, xml.indexOf(',') );
//		String second = xml.substring(xml.indexOf(',') + 1, xml.length() );
		
		String first = "32.881717";
		String second = "-117.233483";

		Log.d("XXX", "XXX haha " + 123 + "," + 321 + " to.." + buddy );

		double lat = Double.parseDouble(first);
		double lon = Double.parseDouble(second);
		
		Log.d("XXX", "XXX lat and lon = " + lat + ',' + lon);
		
		
		Location buddyLoc = new Location("");
		
		buddyLoc.setLatitude(lat);
		buddyLoc.setLongitude(lon);
		
		
		//Store Location object with buddy in HashMap
		buddyLocations.put(buddy, buddyLoc);
		
		Log.d("MAPHELPER", "ADDED LOCATION: " + lat + ',' + lon + "   " + buddy);
		System.out.println("ADDED LOCATION: " + lat + ',' + lon + "   " + buddy);
	}
	
	
	/*
	 * Getter method for buddyLocations
	 */
	public HashMap<String, Location> getBuddyLocations(){
		return buddyLocations;
		
	}
	

}
