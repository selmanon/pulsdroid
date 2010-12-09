package com.pulsradio.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class PlayerInputStreamAAC extends Activity {
	
	private File mediaFile;
	private MediaPlayer mp;
	private String StreamUrl;

	public PlayerInputStreamAAC(String StreamUrl) {
		this.StreamUrl = StreamUrl;
	}
	
	public void play() {
		 
	     try {
	    	 URLConnection cn = new URL(StreamUrl).openConnection();
		        InputStream is = cn.getInputStream();

		        // create file to store audio
		        mediaFile = new File(this.getCacheDir(),"mediafile");
		        FileOutputStream fos = new FileOutputStream(mediaFile);   
		        byte buf[] = new byte[16 * 1024];
		        Log.i("FileOutputStream", "Download");

		        // write to file until complete
		        do {
		                int numread = is.read(buf);   
		            if (numread <= 0)  
		                break;
		            fos.write(buf, 0, numread);
		        } while (true);
		        fos.flush();
		        fos.close();
		        Log.i("FileOutputStream", "Saved");
	    	 mp = new MediaPlayer();

		        // create listener to tidy up after playback complete
		        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
		                public void onCompletion(MediaPlayer mp) {
		                        // free up media player
		                        mp.release();
		                        Log.i("MediaPlayer.OnCompletionListener", "MediaPlayer Released");
		                }
		        };
		        mp.setOnCompletionListener(listener);
		        
			 FileInputStream fis = new FileInputStream(mediaFile);
			 // set mediaplayer data source to file descriptor of input stream
			mp.setDataSource(fis.getFD());
			mp.prepare();
			Log.i("MediaPlayer", "Start Player");
		    mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	}
}
