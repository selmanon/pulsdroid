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
 * @name DialogProgress.java
 * @version 1.0 - 2011/04/03
 * @autor David SANCHEZ
 * 
 * Classe permettant la création d'une boite de dialogue de progression
 */

import android.app.ProgressDialog;
import android.content.Context;


public class DialogProgress {
	
	/**
	 * 
	 */
	private ProgressDialog progressDialog;
	
	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 * @param Context context
	 */
	public DialogProgress(Context context) {
		this.context = context;
		progressDialog = new ProgressDialog(context);
	}
	
	/**
	 * 
	 * @param String message
	 */
	public void startProgress(String message) {
		// On ajoute un message à notre progress dialog
		progressDialog.setMessage(message);

		// On affiche notre message
		progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Boucle de 1 a 10
				for (int i = 0; i < 10; i++) {
					try {
						// Attends 500 millisecondes	
						Thread.sleep(500);	
					} catch (InterruptedException e) {	
						e.printStackTrace();	
					}
				}
		
				// A la fin du traitement, on fait disparaitre notre message
				//progressDialog.dismiss();
			}
		}).start();
	}
	
	/**
	 * 
	 * @param Integer message
	 */
	public void startProgress(int message) {
		// On ajoute un message à notre progress dialog
		progressDialog.setMessage(context.getString(R.string.loading_in_progress));

		// On affiche notre message
		progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// Boucle de 1 a 10
				for (int i = 0; i < 10; i++) {
					try {
						// Attends 500 millisecondes	
						Thread.sleep(500);	
					} catch (InterruptedException e) {	
						e.printStackTrace();	
					}
				}
		
				// A la fin du traitement, on fait disparaitre notre message
				//progressDialog.dismiss();
			}
		}).start();
	}
	
	/**
	 * 
	 */
	public void stopProgress() {
		progressDialog.dismiss();
	}
}
