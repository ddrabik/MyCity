package com.cs110.mycity;

import org.apache.http.client.HttpResponseException;
import android.util.Log;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
 
@SuppressWarnings("deprecation")
public class GooglePlaces {
 
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
 
    // Google API Key
    //old Esther private static final String API_KEY = "AIzaSyDdYmuZ3JTlNVH58IN6dQCwoBEtmgmCe7c";
    //new Esther private static final String API_KEY = "AIzaSyB7ms8DPmJyfSwS4egC9S4BIOVwAcNB2I8";
    //Marian private static final String API_KEY = "AIzaSyDVTZDtGPbx5U1yxCQv_POaYwTvmqK7WDI";
    //Esther private static final String API_KEY = "AIzaSyAiGVwHkWHPjX3JRk5YZ4-etmky1s2tvns";
    //private static final String API_KEY = "AIzaSyDVTZDtGPbx5U1yxCQv_POaYwTvmqK7WDI";
    //private static final String API_KEY = "AIzaSyCMXatpJta-7yxEM5QH6GcDZ2jz2LE-OEQ";
    private static final String API_KEY = "AIzaSyAiGVwHkWHPjX3JRk5YZ4-etmky1s2tvns";
    
 
    // Google Places search url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private double _latitude;
    private double _longitude;
    private double _radius;

    /**
     * Searching places
     * @param latitude - latitude of place
     * @param longitude - longitude of place
     * @param radius - radius of searchable area
     * @param types - type of place to search
     * @return list of places
     * */
    public PlaceList search(double latitude, double longitude, double radius, String types)
            throws Exception {
 
        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;
 
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("radius", _radius); // in meters
            request.getUrl().put("sensor", "false");
            if(types != null)
                request.getUrl().put("types", types);
 
            PlaceList list = request.execute().parseAs(PlaceList.class);
            // Check log cat for places response status
            Log.d("Places Status", "" + list.status);
            return list;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }
 
    }
    
    public PlaceList details(String reference)
    throws Exception {

    	try {

    		HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
    		HttpRequest request = httpRequestFactory
    		.buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
    		request.getUrl().put("key", API_KEY);
    		request.getUrl().put("sensor", "false");
    		request.getUrl().put("reference", reference);

    		PlaceList list = request.execute().parseAs(PlaceList.class);
    		// Check log cat for places response status
    		Log.d("Places Status", "" + list.status);
    		return list;

    	} catch (HttpResponseException e) {
    		Log.e("Error:", e.getMessage());
    		return null;
    	}
    }

   
 
    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            @Override
			public void initialize(HttpRequest request) {
                GoogleHeaders headers = new GoogleHeaders();
                headers.setApplicationName("AndroidHive-Places-Test");
                request.setHeaders(headers);
                JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
                request.addParser(parser);
            }
        });
    }
    

}