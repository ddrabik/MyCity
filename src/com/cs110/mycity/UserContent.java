package com.cs110.mycity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.maps.OverlayItem;
import android.app.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class UserContent extends Activity {
	private String title = "";
	private String info = "";
	private ImageView image = null;

	public static final String ITEM_URI = "http://myteamawesomecity.appspot.com/";
	private static final int SELECT_PHOTO = 100;

	Button btnImage;
	Button btnTitle;
	Button btnInfo;
	Button btnSubmit;

	EditText titleBox;
	EditText infoBox;

	Bitmap yourSelectedImage;
	
	Handler handler;


	//Database push will initiate until the submit button is hit, no earlier.
	//Will also need to happen in a baclground thread to not lock the UI while uploading.



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_content_form);		


		btnImage = (Button) findViewById(R.id.image_button);
		btnTitle = (Button) findViewById(R.id.title_button);
		btnInfo = (Button) findViewById(R.id.info_button);
		btnSubmit = (Button) findViewById(R.id.submit_button);

		titleBox = (EditText) findViewById(R.id.edit_Title);
		infoBox = (EditText) findViewById(R.id.edit_Info);




		btnImage.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				//bring out image gallery or whatever,
				//make image button text change to indicate image has been selected
				//store image to variable

				//				Intent i = new Intent(v.getContext(), BuddyView.class);
				//				startActivity(i);

				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);  

			}


		});

		btnTitle.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				///extract title from box into variable

				String title1 = titleBox.getText().toString();
				System.out.println(" TITLE SET AS: " + title1);
				setTitleString(title1);

			}
		});

		btnInfo.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				///extract info from box into variable
				String title1 = infoBox.getText().toString();
				System.out.println(" INFO SET AS: " + title1);
				setInfoString(title1);
			}
		});




		btnSubmit.setOnClickListener(new View.OnClickListener() {   
			@Override
			public void onClick(View v) {
				//push all three items to the database, have no idea how to upload the image.
				//the text can be done with json
				//need the database to be ready to catch these entities.
				submitAllData();
			}
		});


	}

	//courtesy of bizs09
	//gets the result from the gallery intents and sets the bitmap property to hold it!
	//should be able to upload it, since now we have a reference to it.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("IMAGE FILE NOT FOUND???? CHECK TRACE");
					imageStream = null;
				}
				yourSelectedImage = BitmapFactory.decodeStream(imageStream);
				btnImage.setText("Image Selected");

			}
		}
	}



	
	
	
	
	
	private boolean submitAllData(){
		
		//need some sort of way to package the data and push it to the datastore
		
		/* you can count with:
		 * Bitmap yourSelectedImage
		 * String title
		 * String info
		*/
		
		System.out.println("SUBMITTING DATA TO APPENGINE...");
//		System.out.println(yourSelectedImage.toString().toString());
		System.out.println(title);
		System.out.println(info);
		
		
		
        Thread t = new Thread() {
            
            private OverlayItem item;
       
            @Override
			public void run() {
              HttpClient client = new DefaultHttpClient();
              HttpPost post = new HttpPost(ITEM_URI);
       
              try {
                Log.d("updating", "Updating user content to the server");
       
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("title", item.getTitle()));
                nameValuePairs.add(new BasicNameValuePair("info", item.getSnippet()));
                nameValuePairs.add(new BasicNameValuePair("longitude", ""+ (MappingActivity.loc.getLongitudeE6())));
                nameValuePairs.add(new BasicNameValuePair("latitude", ""+ (MappingActivity.loc.getLatitudeE6())));
                nameValuePairs.add(new BasicNameValuePair("action", "put"));
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
       
                org.apache.http.HttpResponse response = client.execute(post);
       
              } catch (IOException e) {
                Log.d("AddFavorite", "IOException while trying to conect to GAE");
              }
            }
          };
          t.start();
		
		
		
		return true;
	}
	
	





	//GETTERS
	public String getTitleString(){
		return this.title;
	}
	public String getInfoString(){
		return this.info;
	}
	public ImageView getImageView(){
		return this.image;
	}


	//SETTERS
	public void setTitleString(String s){
		this.title = s;
	}
	public void setInfoString(String s){
		this.info = s;
	}
	public void setImageView(ImageView I){
		this.image = I;
	}
	
	
	
	

}
