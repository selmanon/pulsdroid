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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	private Intent intent_news;
	private Intent intent_terms;
	private Intent about_intent;
	private Preference termsPref;
	private Preference aboutPref;
	private Preference newsPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		getListView().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
		
		newsPref = (Preference) findPreference("news");
		newsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				intent_news = new Intent();
			    intent_news.setAction(Intent.ACTION_VIEW);
			    intent_news.addCategory(Intent.CATEGORY_BROWSABLE);
			    intent_news.setData(Uri.parse("http://code.google.com/p/pulsdroid/wiki/WhatsNew"));
			    startActivity(intent_news);
				return true;
			}        	 
         });
		
		aboutPref = (Preference) findPreference("about");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				about_intent = new Intent(Preferences.this, About.class);
                startActivityForResult(about_intent, 0);
				return true;
			}        	 
         });
		
		termsPref = (Preference) findPreference("terms");
		termsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				intent_terms = new Intent();
			    intent_terms.setAction(Intent.ACTION_VIEW);
			    intent_terms.addCategory(Intent.CATEGORY_BROWSABLE);
			    intent_terms.setData(Uri.parse("http://code.google.com/p/pulsdroid/wiki/Terms"));
			    startActivity(intent_terms);
				return true;
			}        	 
         });
	}	
}
