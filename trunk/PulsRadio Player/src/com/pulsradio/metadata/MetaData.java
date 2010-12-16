package com.pulsradio.metadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MetaData {
	
	private URL url;
	private URLConnection conn;
	
	private String Title;
	private String SingleTitle;
	private String[] SingleCutTitle;
	private String BitRate;
	
	public MetaData(String stream_url) {
		try {
			url = new URL (stream_url);
			conn = url.openConnection();
			conn.setRequestProperty("Accept", "*/*"); // TEST
            conn.setRequestProperty ("Icy-Metadata", "1");
            conn.setRequestProperty ("x-audiocast-udpport", "10000");
            conn.setRequestProperty("Connection", "close"); //TEST
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTitle() {
		 Title = conn.getHeaderField(3);
		 return Title;
	}
	
	public String getSingleTitle() {
		SingleTitle = conn.getHeaderField(3);
		SingleCutTitle = SingleTitle.split(" - ");
		return SingleCutTitle[0];
	}
	
	public void getCurrentSong() {
		
	}
	
	public String getBitRate() {
         BitRate = conn.getHeaderField(9);
         return BitRate;
	}
	
}
