package com.pulsradio.pulsdroid;

/**
 * Copyright (C) 2011
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @package com.pulsradio.pulsdroid
 * @name NotificationBar.java
 * @version 1.0 - 2011/04/01
 * @autor David SANCHEZ
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationBar {
	
	/**
	 * 
	 */
	private NotificationManager notificationManager;
	
	/**
	 * 
	 */
	private Context theContext;
	
	/**
	 * 
	 */
	private final int NOTIFICATION_ID = 1010;
	
	/**
	 * 
	 */
	public NotificationBar(Context myContext) {
		this.theContext = myContext;
	}
	
	/**
	 * 
	 * @param create
	 * @param notification_title
	 * @param chan_title
	 * @param chan_song
	 */
	public void triggerNotification(boolean create, String notification_title, String chan_title, String chan_song) {
    	if (create == true) {
    		int icon = R.drawable.icon;        									// icon from resources
    		CharSequence tickerText = notification_title;						// ticker-text
    		long when = System.currentTimeMillis();         					// notification time
    		Context context = theContext.getApplicationContext(); 				// application Context
    		CharSequence contentTitle = chan_title; 							// expanded message title
    		CharSequence contentText = chan_song;     							// expanded message text

    		Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
    		notificationIntent.setClass(context, PulsDroid.class);
    		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
           
    		// the next two lines initialize the Notification, using the configurations above
    		Notification notification = new Notification(icon, tickerText, when);
    		notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
    		
    		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(NOTIFICATION_ID, notification);
    		
    	}
    	else {
    		notificationManager.cancel(NOTIFICATION_ID);
    	}
    }
	
}
