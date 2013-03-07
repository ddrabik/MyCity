package com.cs110.mycity.Chat;

import java.util.HashMap;
import java.util.Random;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cs110.mycity.R;

public class NotificationObserver implements Observer{
	public static final String TAG = "NOTIFICATIONOBSERVER";
	private Context context;
	private NotificationManager mNotificationManager;
	
	private HashMap<String, Integer> notifID = new HashMap<String, Integer>();
	private String excludedBuddy;
	
	public NotificationObserver(Context context) {
		this.context = context;
		mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public void removeNotificationFor(String buddy) {
		if(doesNotificationIDExistFor(buddy)) {
			mNotificationManager.cancel(getNotificationIDFor(buddy));
		}
	}
	
	public void excludeUserFromNotifications(String buddy) {
		this.excludedBuddy = buddy;
	}
	
	public void includeUserInNotifications(String buddy) {
		this.excludedBuddy = (this.excludedBuddy.equals(buddy)) ? "" : this.excludedBuddy;
	}
	

	@Override
	public void update(Subject s) {
		if(s.didRecieveMessage() && ! s.getLastBuddy().equals(excludedBuddy)) {
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.buddy_new_message)
			        .setAutoCancel(true)
			        .setContentTitle(s.getLastBuddy())
			        .setContentText(s.getLastMessage());
			
			Intent resultIntent = new Intent(context, ChatView.class);
			resultIntent.putExtra("SELECTED_BUDDY", s.getLastBuddy());
			
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(ChatView.class);
			
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			
			if(!doesNotificationIDExistFor(s.getLastBuddy())) {
				createNotificationIDFor(s.getLastBuddy());
			}
			
			mNotificationManager.notify(getNotificationIDFor(s.getLastBuddy()), mBuilder.build());
		}
	}
	
	private boolean doesNotificationIDExistFor(String user) {
		return notifID.containsKey(user);
	}
	
	private Integer getNotificationIDFor(String user) {
		return notifID.get(user);
	}
	
	private void createNotificationIDFor(String user) {
		if(!notifID.containsKey(user)) {
			notifID.put(user, getRandomInteger());
		}
	}
	
	private Integer getRandomInteger() {
		Random generator = new Random();
		return generator.nextInt();
	}

	
}
