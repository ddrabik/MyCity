package com.cs110.mycity.Chat;

import java.util.ArrayList;

public interface Subject {
	public void registerObserver(Observer o);
	public void removeObserver(Observer o);
	
	public ArrayList<String> getConversationWith(String buddy);
	public String getLastBuddy();
	public String getLastMessage();
	public boolean didRecieveMessage();
}
