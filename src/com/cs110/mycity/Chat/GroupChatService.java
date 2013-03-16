package com.cs110.mycity.Chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.cs110.mycity.R;

public class GroupChatService extends Service{
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void receivedInvitation(String msg)
    {
        Log.i("receivedInvitation", "Creating Notification");
        GroupChatController.join = true;
        GroupChatController.room = msg;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent i = new Intent(this, GroupChatView.class);
        Log.i("receivedInvitation", "Intent Number: "+ i.getStringExtra("room"));
        Log.i("receivedInvitation", "Room Number: "+ msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i,0);
        Notification notification = new Notification.Builder(this)
        .setContentTitle("Some one invite you to Group Chat")
        .setContentText("Click To Join")
        .setSmallIcon(R.drawable.city_icon)
        .setContentIntent(contentIntent)
        .setSound(soundUri).build();
      
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        
    }
}
