package com.cs110.mycity;

import java.util.List;

import com.google.api.client.util.Key;

public class PlaceList {
	 
    @Key
    public String status;
 
    @Key
    public List<Place> results;
 
}