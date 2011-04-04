package com.pulsradio.pulsdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	/**
	 * Appelé lorsque l'activité est d'abord créé.
	 * Permet de charger le fichier XML des préférences.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}	
}
