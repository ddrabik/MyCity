package com.cs110.tests;

import android.location.Location;
import android.test.*;

import com.cs110.mycity.*;
import com.google.android.maps.GeoPoint;

public class MappingActivityTests extends ActivityInstrumentationTestCase2<MappingActivity> {

	MappingActivity map;
	
	@SuppressWarnings("deprecation")
	public MappingActivityTests() {
		super("com.cs110.mycity", MappingActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		map = getActivity();

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testGetBestProvider() {
		assertNotNull(map.getBestProvider());
	}

	public void testSetCurrentLocation() {
		assertNull(map.getCurrentLocation());
	    GeoPoint currentPoint = new GeoPoint((int)33.71463,(int)-116.188774);
	    Location currentLocation = new Location("");
	    currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
	    currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	    map.setCurrentLocation(currentLocation);
	    assertEquals(map.getCurrentLocation().toString(),currentLocation.toString());
	    assertEquals(map.getCurrentPoint(),currentPoint);
	}

	public void testOnLocationChanged() {
	    GeoPoint currentPoint = new GeoPoint((int)34.5237,(int)-113.188774);
	    Location newLocation = new Location("");
	    newLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
	    newLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
	    map.onLocationChanged(newLocation);
	    assertEquals(map.getCurrentLocation().toString(),newLocation.toString());
	}

	public void testDrawCurrPositionOverlay() {
		if(map.getCurrentPoint() == null) {
			map.drawCurrPositionOverlay();
			assertNull(map.getCurrentPoint());
		}
		else {
			Location previousLocation = map.getCurrentLocation();
			map.drawCurrPositionOverlay();
			assertNotNull(map.getCurrPos());
			assertEquals(map.getCurrentLocation(),previousLocation);
			assertNotNull(map.getCurrentPoint());
		}
	}

}
