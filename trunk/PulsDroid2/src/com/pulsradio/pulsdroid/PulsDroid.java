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
 * @name PulsDroid.java
 * @version 1.0 - 2011/04/03
 * @autor David SANCHEZ
 * 
 * Test		: 2.1U1		
 * 			: 2.2		WORKING !
 * 			: 2.3		WORKING !
 * 
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pulsradio.pulsdroid.mediaplayer.mp3player.MP3Player;
import com.spoledge.aacplayer.DirectAACPlayer;
import com.spoledge.aacplayer.FAADDecoder;
import com.spoledge.aacplayer.FFMPEGDecoder;

public class PulsDroid extends Options_Menu implements View.OnClickListener {
	
	/**
	 * Déclaration d'un attribut de type Sharedpreferences
	 */
	private SharedPreferences sharedPreferences;
	
	/**
	 * 
	 */
	private boolean terms;
	
	/**
	 * Déclaration d'un attribut de la classe DialogBox
	 */
	private DialogBox termsBox, dataBox;
	
	/**
	 * Déclaration d'un attribut contenant le type de lien à charger
	 */
	private String BANDWIDTH;
	
	/**
	 * 
	 */
	private String LOWSPEEDCONNECTOR;
	
	/**
	 * Déclaration des boutons
	 */
	private Button play;
	private Button pause;
	private Button stop;
	
	/**
	 * Déclaration des URLs du canal Puls'Radio
	 */
	private String radio_aac32 = "http://aac32.pulsradio.com:5055";
	private String radio_aac80 = "http://rps.pulsplayer.com:8080";
	private String radio_gen = "http://stream.pulsradio.com:5000";
	private String radio_firewall = "http://firewall.pulsradio.com:80";
	
	/**
	 * Déclaration des URLs du canal Puls'90
	 */
	private String ninety_aac = "http://relay.pulsradio.com:7050";
	private String ninety_gen = "http://stream90.pulsradio.com:7000";
	
	/**
	 * Déclaration des URLs du canal Puls'80
	 */
	private String eighty_aac = "http://relay.pulsradio.com:6050";
	private String eighty_gen = "http://stream80.pulsradio.com:6000";
	
	/**
	 * Déclaration des URLs du canal Puls'Trance
	 */
	private String trance_aac = "http://relay.pulsradio.com:9050";
	private String trance_gen = "http://stream.trance.pulsradio.com:9000";
	private String trance_firewall = "http://firewall.trance.pulsradio.com:80";
	
	/**
	 * Déclarations variables pour la lecture du stream
	 */
	private MP3Player mp3Player;
	
	/**
	 * 
	 */
	 private DirectAACPlayer aacPlayer;
	
    /**
     * Méthode appelé lorsque l'activité est d'abord créé.
     * 
     * @apilevel 1
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /**
         * Déclaration des objets pour la lecture des flux MP3 et AAC.
         */
        mp3Player = new MP3Player(PulsDroid.this);
        
        /**
         * Récupération des préférences de l'application.
         */
        getPreferences();
        
        /**
         * Création des boutons.
         */
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);        
        
        /**
         * Appel de la méthode onClick dès qu'une action sur un des boutons est effectuée.
         */
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
           
        /**
         * Affichage de la boite de dialogue
         */
        termsBox = new DialogBox(this);
        dataBox = new DialogBox(this);
        
        /**
         * 
         */
        termsBox.setTitle(R.string.terms_of_use);
        termsBox.setMessage(R.string.terms_of_use_explain);
        termsBox.setPositiveButton(R.string.i_agree, "terms");
        termsBox.setNegativeButton(R.string.i_disagree);
        if (terms == false) {
        	termsBox.show();
        }
        
        /**
         * On vérifie si l'utilisateur à un accès à internet pour pouvoir utiliser l'application
         * Sinon, on désactive les boutons
         */
        if (haveInternet()) {
        	play.setEnabled(true);
        	pause.setEnabled(true);
            stop.setEnabled(true);
        }
        else {
        	play.setEnabled(false);
        	pause.setEnabled(false);
            stop.setEnabled(false);            
          
            dataBox.setTitle(R.string.no_data_detected);
            dataBox.setMessage(R.string.no_data_detected_explain);
            dataBox.setNegativeButton(R.string.ok);
            dataBox.show();
        }
    }
    
    /**
	 * Méthode appelé lors d'une action sur un des boutons.
	 */
	public void onClick( View v ) {
        try {
            switch (v.getId()) {
            	/**
            	 * Définition de l'action pour le bouton "play"
            	 */
                case R.id.play:
                	if (BANDWIDTH.equals("veryhighspeed")) {
                		mp3Player.play(radio_gen);
                	}
                	else if (BANDWIDTH.equals("highspeed")) {
                		aacPlayer = new DirectAACPlayer(this);
                		if (LOWSPEEDCONNECTOR.equals("faaddecoder")) {
                			aacPlayer.playAsync(radio_aac80, FAADDecoder.create());
                		}
                		else if (LOWSPEEDCONNECTOR.equals("ffmpegdecoder")) {
                			aacPlayer.playAsync(radio_aac80, FFMPEGDecoder.create());
                		}
                	}
                	else if (BANDWIDTH.equals("lowspeed")) {
                		aacPlayer = new DirectAACPlayer(this);
                		if (LOWSPEEDCONNECTOR.equals("faaddecoder")) {
                			aacPlayer.playAsync(radio_aac32, FAADDecoder.create());
                		}
                		else if (LOWSPEEDCONNECTOR.equals("ffmpegdecoder")) {
                			aacPlayer.playAsync(radio_aac32, FFMPEGDecoder.create());
                		}
                	}
                	else if (BANDWIDTH.equals("firewall")) {
                		mp3Player.play(radio_firewall);
                	}
                    break;
                    
                /**
                 * Définition de l'action pour le bouton "pause"
                 */
                case R.id.pause:
                	if (BANDWIDTH.equals("veryhighspeed") || BANDWIDTH.equals("firewall")) {
                		mp3Player.pause();
                	}
                	else if (BANDWIDTH.equals("lowspeed") || BANDWIDTH.equals("highspeed")) {
                		
                	}
                    break;

                /**
                 * Définition de l'action pour le bouton "stop"
                 */
                case R.id.stop:
                	if (BANDWIDTH.equals("veryhighspeed") || BANDWIDTH.equals("firewall")) {
                		mp3Player.stop();
                	}
                	else if (BANDWIDTH.equals("lowspeed") || BANDWIDTH.equals("highspeed")) {
                		aacPlayer.stop();
                		aacPlayer = null;
                	}
                    break;
            }
        }
        catch (Exception e) {
            Log.e("onClick", "exception" , e);
        }
    }
    
    /**
     * Méthode permettant d'actualiser les préférences.
     * 
     * @apilevel 1
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == CODE_RETOUR) {
    		Toast.makeText(this, R.string.saved_settings, Toast.LENGTH_SHORT).show();
    		this.getPreferences();
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
	
	 /**
     * Méthode permettant de récupérer la valeur des préférences
     */
    public void getPreferences() {
    	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		BANDWIDTH = sharedPreferences.getString("bandwidth", "highspeed");
		LOWSPEEDCONNECTOR = sharedPreferences.getString("lowspeedconnector", "faaddecoder");
		terms = sharedPreferences.getBoolean("terms", false);
	}

    /**
     * On vérifie si l'utilisateur à appuyé sur la touche "back"
     * 
     * @apilevel 1
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	// On affiche une boite de dialogue pour demander à l'utilisateur s'il veut quitter
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.really_quit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Arret de l'activitée en cours
                	if (mp3Player.isPlaying() == true) {
            			mp3Player.stop();
            		}
                    PulsDroid.this.finish();
                    
                }
            })
            .setNegativeButton(R.string.no, null).show();
            return false;
        }
        else {
        	return super.onKeyUp(keyCode, event);
        }      
    }
    
    /**
     * Désactive le bouton "Retour"
     * 
     * @apilevel 5
     */
    @Override
    public void onBackPressed() {
       return;
    }
    
    /**
     * Vérification d'un connection à internet pour l'appareil
     * 
     * @return true si l'appareil a internet
     * @source http://www.androidsnippets.org/snippets/131/
     */
    public boolean haveInternet() {
  	
    	NetworkInfo info = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }
        
        if (info.isRoaming()) {
        	return false;
        }
        return true;
    }

}