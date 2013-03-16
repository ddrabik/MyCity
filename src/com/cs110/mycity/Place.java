package com.cs110.mycity;
import com.google.api.client.util.Key;

public class Place {
	 @Key
	    public String id;
	 
	    @Key
	    public String name;
	 
	    @Key
	    public String reference;
	 
	    @Key
	    public String icon;
	 
	    @Key
	    public String vicinity;
	 
	    @Key
	    public Geometry geometry;
	 
	    @Key
	    public String formatted_address;
	 
	    @Key
	    public String formatted_phone_number;
	  
	    
	    @Key
	    public String website;
	 
	    @Override
	    public String toString() {
	        return name + " - " + id + " - " + reference;
	    }
	 
	    public static class Geometry
	    {
	        @Key
	        public Location location;
	    }
	 
	    public static class Location
	    {
	        @Key
	        public double lat;
	 
	        @Key
	        public double lng;
	    }
}