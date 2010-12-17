package com.pulsradio.pulsdroid;

/**
 * Copyright (C) 2010 <David SANCHEZ>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.pulsradio.irc.ChanIRC;
import com.pulsradio.metadata.MetaData;
import com.pulsradio.stream.PlayerInputStream;

public class PulsDroid extends Activity {
	
	private PlayerInputStream PlayerStream;
	private Button play;
	private Button pause;
	private Button stop;
	private MenuInflater inflater;
	private SharedPreferences sp;
	private StringBuilder nick_prefs;
	private boolean low_stream;
	private NotificationManager notificationManager;
	private final int NOTIFICATION_ID = 1010;
	private ListView l1;
	private MetaData metadata;
	private boolean isPlaying = false;
	private String url;
	
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        low_stream = sp.getBoolean("low_stream", true);        

        l1 = (ListView) findViewById(R.id.ListView01);
		l1.setAdapter(new EfficientAdapter(this));
		l1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub				

				for(int i=0; i<parent.getChildCount(); i++) {
					if(position == 0) {
						parent.getChildAt(position).setBackgroundResource(R.drawable.alternative_orange);
						parent.getChildAt(1).setBackgroundDrawable(null);
						parent.getChildAt(2).setBackgroundDrawable(null);
						parent.getChildAt(3).setBackgroundDrawable(null);
					}
					
					if(position == 1) {
						parent.getChildAt(position).setBackgroundResource(R.drawable.alternative_purple);
						parent.getChildAt(0).setBackgroundDrawable(null);
						parent.getChildAt(2).setBackgroundDrawable(null);
						parent.getChildAt(3).setBackgroundDrawable(null);
					}
					
					if(position == 2) {
						parent.getChildAt(position).setBackgroundResource(R.drawable.alternative_green);
						parent.getChildAt(0).setBackgroundDrawable(null);
						parent.getChildAt(1).setBackgroundDrawable(null);
						parent.getChildAt(3).setBackgroundDrawable(null);
					}

					if(position == 3) {
						parent.getChildAt(position).setBackgroundResource(R.drawable.alternative_blue);
						parent.getChildAt(0).setBackgroundDrawable(null);
						parent.getChildAt(1).setBackgroundDrawable(null);
						parent.getChildAt(2).setBackgroundDrawable(null);
					}

		         }

				if (EfficientAdapter.country[position] == "Puls'Radio") {
					Toast.makeText(getBaseContext(), "You choose Puls'Radio channel !", Toast.LENGTH_SHORT).show();
					
					if (low_stream == true) {
						
					}
					else {
						url = "http://stream.pulsradio.com:5000/";
					}
					
				}
				else if (EfficientAdapter.country[position] == "Puls'80") {
					Toast.makeText(getBaseContext(), "You choose Puls'80 channel !", Toast.LENGTH_SHORT).show();
					
					if (low_stream == true) {
						
					}
					else {
						url = "http://stream80.pulsradio.com:6000/";
					}
					
				}
				else if (EfficientAdapter.country[position] == "Puls'90") {
					Toast.makeText(getBaseContext(), "You choose Puls'90 channel !", Toast.LENGTH_SHORT).show();
					
					if (low_stream == true) {
						
					}
					else {
						url = "http://stream90.pulsradio.com:7000/";
					}
					
				}
				else if (EfficientAdapter.country[position] == "Puls'Trance") {
					Toast.makeText(getBaseContext(), "You choose Puls'Trance channel !", Toast.LENGTH_SHORT).show();
					
					if (low_stream == true) {
						
					}
					else {
						url = "http://stream.trance.pulsradio.com:9000/";
					}
					
				}
				
				if(isPlaying) {
		    		PlayerStream.stop();
		    		triggerNotification(false, "", "", "");
		    		PlayerStream = new PlayerInputStream(url);
		    		PlayerStream.play();
		    		metadata = new MetaData(url);
		    		triggerNotification(true, metadata.getTitle(), metadata.getSingleTitle(), ""); 
		    	}
		    	else {
		    		PlayerStream = new PlayerInputStream(url);
		    		PlayerStream.play();
		    		metadata = new MetaData(url);
		    		triggerNotification(true, metadata.getTitle(), metadata.getSingleTitle(), ""); 
		    		isPlaying = true;
		    	}
			}
			
		 });
		
		if (url != "") {

	        play = (Button) findViewById(R.id.play);
	        play.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	            	PlayerStream.play();
	            	triggerNotification(true, metadata.getTitle(), metadata.getSingleTitle(), "");           	
	            }
	        });

	        pause = (Button) findViewById(R.id.pause);
	        pause.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	            	PlayerStream.pause();
	            }
	        });

	        stop = (Button) findViewById(R.id.stop);
	        stop.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	            	triggerNotification(false, "", "", "");
	            	PlayerStream.stop();
	            }
	        });
		}

        nick_prefs = new StringBuilder();
        nick_prefs.append(sp.getString("irc_nickname","NULL"));
        	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	setMenuBackground();
    	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.irc:
	    		Intent i = new Intent(this, ChanIRC.class);
	    		i.putExtra("nick", nick_prefs.toString());
	    		startActivity(i);
	    	break;
	    	
	    	case R.id.settings:
	    		startActivity(new Intent(this, Preferences.class));
            break;
    	}
    	return false;
    }

	protected void setMenuBackground(){
		getLayoutInflater().setFactory( new Factory() {
			
			@Override
			public View onCreateView ( String name, Context context, AttributeSet attrs ) {
				if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
					try { // Ask our inflater to create the view
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView( name, null, attrs );
						/*
						 * The background gets refreshed each time a new item is added the options menu.
						 * So each time Android applies the default background we need to set our own
						 * background. This is done using a thread giving the background change as runnable
						 * object
						 */
						new Handler().post( new Runnable() {
							public void run () {
								view.setBackgroundResource(R.drawable.menu_background);
							}
						});
						return view;
					}
					catch ( InflateException e ) {}
					catch ( ClassNotFoundException e ) {}
				}
				return null;
			}
		});
	}

	private void triggerNotification(boolean create, String notification_title, String chan_title, String chan_song) {
    	if (create == true) {
    		int icon = R.drawable.icon;        									// icon from resources
    		CharSequence tickerText = notification_title;						// ticker-text
    		long when = System.currentTimeMillis();         					// notification time
    		Context context = getApplicationContext(); 							// application Context
    		CharSequence contentTitle = chan_title; 							// expanded message title
    		CharSequence contentText = chan_song;     							// expanded message text

    		Intent notificationIntent = new Intent(this, PulsDroid.class);
    		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
           
    		// the next two lines initialize the Notification, using the configurations above
    		Notification notification = new Notification(icon, tickerText, when);
    		notification.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
    		
    		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE); 		
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(NOTIFICATION_ID, notification);
    		
    	}
    	else {
    		notificationManager.cancel(NOTIFICATION_ID);
    	}
    }

	
}