package com.pulsradio.pulsdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	/**
	 * Appel� lorsque l'activit� est d'abord cr��.
	 * Permet de charger le fichier XML des pr�f�rences.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}	
}
