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
 * @name DialogBox.java
 * @version 1.0 - 2011/04/02
 * @autor David SANCHEZ
 * 
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;

public class DialogBox {
	
	/**
	 * D�claration d'un objet permettant l'int�raction avec les pr�f�rences partag�es.
	 */
	private SharedPreferences sharedPreferences;
	
	/**
	 * 
	 */
	private AlertDialog.Builder alertBox;
	
	/**
	 * 
	 */
	private Context context;

	/**
	 * Constructeur de la classe DialogBox
	 * Elle contient le code permettant l'affichage d'une
	 * boite de dialogue avec les termes d'utilisation de l'application.
	 * 
	 * @param context
	 */
	public DialogBox(final Context context) {
		this.context = context;
		
		/**
		 * 
		 */
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		/**
		 * Cr�ation de la boite de dialogue
		 */
		alertBox = new AlertDialog.Builder(context);      
	}
	
	/**
	 * Ajout du titre � la boite de dialogue
	 * 
	 * @param title
	 */
	public void setTitle(int title) {
		alertBox.setTitle(title);
	}
	
	/**
	 * Ajout du message principal � la boite de dialogue
	 * @param message
	 */
	public void setMessage(int message) {
		alertBox.setMessage(Html.fromHtml(context.getResources().getString(message)));
	}
	
	/**
	 * Ajout d'un bouton positif
	 * Mise � jour de la pr�f�rences
	 * 
	 * @param message
	 * @apilevel 1
	 */
	public void setPositiveButton(int message, final String prefs) {
		alertBox.setPositiveButton(message, new DialogInterface.OnClickListener() {
            // Click listener 
			@Override
            public void onClick(DialogInterface dialog, int id) {
            	/**
            	 * Met � jour la pr�f�rences "terms" par le parama�tre true
            	 * Cela permettra de ne plus afficher les termes d'utilisation de l'application.
            	 */
            	sharedPreferences.edit().putBoolean(prefs, true).commit();            	
            }
        });
	}
	
	/**
	 * Ajout d'un bouton positif
	 * 
	 * @param message
	 * @apilevel 1
	 */
	public void setPositiveButton(int message) {
		alertBox.setPositiveButton(message, new DialogInterface.OnClickListener() {
            // Click listener 
			@Override
            public void onClick(DialogInterface dialog, int id) {
            	       	
            }
        });
	}
	
	/**
     * Ajout d'un bouton n�gatif
     * 
     * @param message
     * @apilevel 1
     */
	public void setNegativeButton(int message) {
		alertBox.setNegativeButton(message, new DialogInterface.OnClickListener() {
            // Click listener
			@Override
            public void onClick(DialogInterface dialog, int id) {
            	/**
            	 * Permet de fermer l'application si l'utilisateur refuse les termes
            	 */
            	((Activity) context).finish();
            }
        });
	}
	
	/**
	 * 
	 * @param message
	 * @apilevel 1
	 */
	public void setNeutralButton(int message) {
		alertBox.setNeutralButton(message, new DialogInterface.OnClickListener() {
			// Click listener 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/**
            	 * Permet de fermer l'application si l'utilisateur refuse les termes
            	 */
            	((Activity) context).finish();			
			}
		});
	}
	
	/**
     * Affichage de la boite de dialogue
     */
	public void show() {
		alertBox.show();
	}
}
