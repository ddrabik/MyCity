package com.cs110.mycity.Chat;

import android.content.Context;

public class ChatController implements Observer {

	protected static final String TAG = "CHATCONTROLLER";
	private SocketListener mService = null;
	private Context context;
	private String currentViewBuddy;
	private NotificationObserver notif;

	public ChatController(Context context, String currentViewBuddy, SocketListener mService) {
		this.context = context;
		this.currentViewBuddy = currentViewBuddy;
		this.mService = mService;

		notif = mService.getNotificationObserver();

		resetNotification(currentViewBuddy);
		excludeUserFromNotification(currentViewBuddy);
	}


	public void sendMessage(String text) {
		mService.sendMessageTo(currentViewBuddy, text);
	}

	public void conversationViewed() {
		mService.resetBuddyPresence(currentViewBuddy);
		includeUserInNotifications(currentViewBuddy);
	}

	public void excludeUserFromNotification(String buddy) {
		notif.excludeUserFromNotifications(buddy);
	}

	public void includeUserInNotifications( String buddy) {
		notif.includeUserInNotifications(buddy);
	}

	public void resetNotification(String buddy) {
		notif.removeNotificationFor(buddy);
	}



	@Override
	public void update(Subject s) {

	}

}
