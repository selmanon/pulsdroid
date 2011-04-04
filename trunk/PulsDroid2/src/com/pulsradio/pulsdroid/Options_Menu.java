package com.pulsradio.pulsdroid;

/**
 * Copyright (C) 2011 <David SANCHEZ>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Options_Menu extends Activity {
	
	/**
	 * 
	 */
	protected static int CODE_RETOUR = 1;

	/**
     * Méthode permettant la création d'un menu
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main_menu, menu);
    	setMenuBackground();
    	return super.onCreateOptionsMenu(menu);
	}

    /**
     * Méthode permettant la gestion des évenements des boutons du menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.itemSettings) {
    		startActivityForResult(new Intent(this, Preferences.class), CODE_RETOUR);
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    /**
	 * La classe IconMenuItemView permet de créer et de contrôler les options
	 * dérivées du menu de la classe de base.
	 * Ansi nous pouvons utiliser un objet LayoutInflater pour
	 * créer une vue et afficher l'arrière-plan.
     */
	protected void setMenuBackground(){
		getLayoutInflater().setFactory( new Factory() {
			
			@Override
			public View onCreateView ( String name, Context context, AttributeSet attrs ) {
				if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
					try { // Ask our inflater to create the view
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView( name, null, attrs );
						/**
						 * L'arrière-plan est rafraichi à chaque fois qu'un nouvel élément est ajouté aux optiosn du menu.
						 * Donc, à chaque fois qu'Android applique le fond par défaut, nous devons définir notre propre arrière-plan.
						 * Ceci est fait en utilisant un thread donnant l'évolution de l'arrière-plan grâce à un objet Runnable.
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
}
