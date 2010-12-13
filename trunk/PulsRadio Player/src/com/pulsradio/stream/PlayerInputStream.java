package com.pulsradio.stream;

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
 * 
 * Sources : http://efreedom.com/Question/1-3595491/Android-22-HTTP-Progressive-Streaming-HTTP-Live-Streaming
 */

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class PlayerInputStream extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

	/**
	 * Déclaration de l'objet mp
	 * C'est un objet de la classe MediaPlayer
	 */
	private MediaPlayer mp;
	
	/**
	 * 
	 */
	private String TAG = getClass().getSimpleName();

	/**
	 * Déclaration de la variable url de stype string
	 * Initialisée à null
	 */
	private String url = null;
	
	private int buffer_percent = 0;
	
	/**
	 * Constructeur de la classe PlayerInputStream
	 * On passera en paramêtre l'URL du stream qui
	 * sera transmit à la variable "url"
	 * 
	 * @param StreamUrl
	 */
	public PlayerInputStream(String StreamUrl) {
		this.url = StreamUrl;
		
	}
	
	/**
	 * Méthode play() de type void
	 * Création de l'objet mp de la classe MediaPlayer()
	 * 
	 */
	public void play(){
		try {
			
			if (mp == null) {
	    		mp = new MediaPlayer();
	    	} else {
	    		mp.stop();
	    		mp.reset();
	    	}
			
			mp.setDataSource(this.url);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mp.setOnPreparedListener(this);
	    	mp.setOnBufferingUpdateListener(this);
	    	
	    	mp.setOnErrorListener(this);
	    	mp.prepareAsync();
	    	
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Go to Initialized state

	}
	
	/**
	 * 
	 */
	public void pause() {
		mp.pause();

	}
	
	/**
	 * 
	 */
	public void stop() {
		mp.stop();
		mp.reset();
	}
	
	public void start() {
		mp.start();
	}
	
	/**
	 * 
	 */
	@Override
    public void onPrepared(MediaPlayer mp) {
		mp.start();
	}
	
	/**
	 * 
	 */
	public void onDestroy() {
		super.onDestroy();
    	stop();
	}
	
	/**
	 * 
	 */
	public void onCompletion(MediaPlayer mp) {
		mp.stop();
	}
	
	/**
	 * 
	 */
	public boolean onError(MediaPlayer mp, int what, int extra) {
		StringBuilder sb = new StringBuilder();
  	  
    	sb.append("Media Player Error: ");
    	switch (what) {
    		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
    			sb.append("Not Valid for Progressive Playback");
    			break;
    		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
    			sb.append("Server Died");
    			break;
    		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
    			sb.append("Unknown");
    			break;
    		default:
    			sb.append(" Non standard (");
    			sb.append(what);
    			sb.append(")");
    	}
    	sb.append(" (" + what + ") ");
    	sb.append(extra);
    	Log.e(TAG, sb.toString());
    	
    	return true;	
	}
	
	public int getBufferPercentage() {
		return buffer_percent;
	}
	
	/**
	 * 
	 */
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		buffer_percent = percent;
		Log.d(TAG, "onBufferingUpdate percent:" + percent);
	}
	

}
