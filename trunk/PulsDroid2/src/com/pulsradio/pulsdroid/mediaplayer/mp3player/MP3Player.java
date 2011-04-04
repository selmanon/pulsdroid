package com.pulsradio.pulsdroid.mediaplayer.mp3player;

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
 * @package com.pulsradio.pulsdroid.mediaplayer.mp3player
 * @name PulsDroid.java
 * @version 1.0 - 2011/04/03
 * @source http://stackoverflow.com/questions/3595491/is-android-2-2-http-progressive-streaming-http-live-streaming
 * @autor David SANCHEZ
 * 
 * Test		: 2.1U1		NOT WORKING !
 * 			: 2.2		WORKING !
 * 			: 2.3		WORKING !
 * 
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.pulsradio.pulsdroid.DialogProgress;
import com.pulsradio.pulsdroid.MessageManager;
import com.pulsradio.pulsdroid.NotificationBar;
import com.pulsradio.pulsdroid.R;

public class MP3Player implements

	/**
	 * Implémentation des différentes interface de la classe MediaPlayer
	 */
	MediaPlayer.OnCompletionListener,
	MediaPlayer.OnPreparedListener,
	MediaPlayer.OnErrorListener,
	MediaPlayer.OnBufferingUpdateListener {

	/**
	 * 
	 */
	private String TAG = "MP3Player";
	private MediaPlayer mp = null;
	private Context context;
	private Uri myUri;
	private NotificationBar notificationBar;
	
	/**
	 * 
	 */
	private boolean error = false;
	
	/**
	 * 
	 */
	private DialogProgress dialogProgress;
    
	/**
	 * Constructeur de la classe MP3Player.
	 * Passage en paramètre du Context de la classe principal.
	 * 
	 * @param Context myContext
	 */
    public MP3Player(Context context) {
    	this.context = context;
    	new MessageManager(this.context).execute("");
    	notificationBar = new NotificationBar(this.context);
    	
        /**
         * 
         */
        dialogProgress = new DialogProgress(context);
    }
    
    /**
     * Méthode play() permettant de lancer la lecture du flux.
     * Passage en paramètre de l'url du flux streamé.
     * 
     * @param String url
     */
    public void play(String url) {
    	try {
    		myUri = Uri.parse(url);
    		
    		if (mp == null) {
    			this.mp = new MediaPlayer();
    		} else {
    			mp.stop();
    			mp.reset();
    		}
   		  		
    		mp.setDataSource(this.context, myUri); // Go to Initialized state
    		mp.prepareAsync();
    		mp.setOnBufferingUpdateListener(this);
    		mp.setOnCompletionListener(this);
    		mp.setOnPreparedListener(this);
    		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    		mp.setOnErrorListener(this);
    		
    		if (!error) {
    			Log.d(TAG, "Load audio streaming");
    			dialogProgress.startProgress(R.string.loading_in_progress);
    		}
    	} catch (Throwable t) {
    		Log.d(TAG, t.toString());
    		Toast.makeText(this.context, t.toString(), Toast.LENGTH_SHORT).show();
    	}
    }
    
    /**
     * Méthode pause() permettant de mettre en pause la lecture du flux.
     * 
     * @apilevel 1
     */
    public void pause() {
    	mp.pause();
	}
    
    /**
     * Méthode stop() permettant de stopper la lecture du flux.
     * 
     * @apilevel 1
     */
    public void stop() {
    	mp.stop();
    	if (!mp.isPlaying()) {
    		Toast.makeText(this.context, "Player arrété !!!", Toast.LENGTH_SHORT).show();
    		notificationBar.triggerNotification(false, "", "", "");
    	}
	}
    
    /**
     * Méthode permettant de savoir si la lecture est en cours.
     * 
     * @return boolean isPlaying
     * @apilevel 1
     */
    public boolean isPlaying() {
    	return mp.isPlaying();
    }

    /**
     * 
     * @apilevel 1
     */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		//Log.d(TAG, "PlayerService onBufferingUpdate : " + percent + "%");
	}

	/**
	 * Méthode permettant la gestion des erreurs
	 * Affichage d'un "Toast" pour afficher les messages d'erreurs
	 * 
	 * @retourn boolean true
	 * @apilevel 1
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		StringBuilder wsb = new StringBuilder();
		wsb.append(context.getString(R.string.error_media_player) + " ");
		
		/** 
		 * Gestion des errors par rapport à la variable what
		 * Erreurs prédéfinies dans le SDK
		 */
		switch (what) {
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				wsb.append(context.getString(R.string.error_not_valid_for_progressive_playback));
				break;
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				wsb.append(context.getString(R.string.error_server_died));
				break;
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				wsb.append(context.getString(R.string.error_unknown));
				break;
			default:
				wsb.append(context.getString(R.string.error_non_stantard));
				wsb.append(what);
				wsb.append(")");
		}
		wsb.append(" (" + what + ") ");
		
		/**
		 * Gestion des erreurs par rapport à la variable extra
		 * Erreurs personnalisées
		 */
		StringBuilder esb = new StringBuilder();
		switch (extra) {
			case -1002:
				esb.append(context.getString(R.string.error_unable_to_connect));
				this.error = true;
				dialogProgress.stopProgress();
				break;
			default:
				break;
		}
		
		wsb.append(extra);
		
		if (!wsb.toString().equals("")) {
			Log.e(TAG, wsb.toString());
			Toast.makeText(this.context, wsb.toString(), Toast.LENGTH_LONG).show();
		}
		if (!esb.toString().equals("")) {
			Log.e(TAG, esb.toString());
			Toast.makeText(this.context, esb.toString(), Toast.LENGTH_LONG).show();
		}
		
		return true;
	}

	/**
	 * 
	 * @apilevel 1
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Stream is prepared");
		mp.start();
		if (mp.isPlaying()) {
			dialogProgress.stopProgress();
			notificationBar.triggerNotification(true, "Puls'Droid", "Titre", "Musique");
		}
	}

	/**
	 * 
	 * @apilevel 1
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		stop();
	}
	
	/**
	 * 
	 */
	public void onDestroy() {
		onDestroy();
		stop();
	}
}