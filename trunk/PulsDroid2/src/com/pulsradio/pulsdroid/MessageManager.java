package com.pulsradio.pulsdroid;

import android.content.Context;
import android.os.AsyncTask;

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
 * @name MessageManager.java
 * @version 1.0 - 2011/04/01
 * @autor David SANCHEZ
 * 
 */

public class MessageManager extends AsyncTask<String, String, String> {
	
	private Context context;
	
	/**
	 * 
	 */
	public MessageManager(Context myContext) {
		this.context = myContext;
	}

	protected String doInBackground(String... string) {
		// TODO Auto-generated method stub
		//Toast.makeText(this.context, "Test", Toast.LENGTH_LONG).show();
		
		return null;
	}

}
