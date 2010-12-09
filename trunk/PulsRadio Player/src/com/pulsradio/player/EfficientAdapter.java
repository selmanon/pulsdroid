package com.pulsradio.player;

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
 * Sources : http://herewe.servebeer.com/clinet/
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EfficientAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public static final String[] country = {"Puls\'Radio", "Puls\'80", "Puls\'90", "Puls\'Trance"};
	private static final String[] curr = {"Rocco And Bass-T - Players In A Frame", "Steve Allen - Letter from my heart", "Drop Kickz - Bring Da Noize", "Rednoise - Tears In Rain"};


	public EfficientAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		return country.length;
	}
	
	public Object getItem(int position) {
		return position;
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.TextView01);
			holder.text2 = (TextView) convertView.findViewById(R.id.TextView02);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		 holder.text.setText(country[position]);
		 holder.text2.setText(curr[position]);
		
		 return convertView;
	 }
	
	 static class ViewHolder {
		 TextView text;
		 TextView text2;
	 }
}
