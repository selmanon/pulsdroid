package com.spoledge.aacplayer;

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
 * @package com.spoledge.aacplayer
 * @name DirectAACPlayer.java
 * @version 1.0 - 2011/04/03
 * @autor David SANCHEZ
 * @source http://code.google.com/p/aacplayer-android/
 * 
 * This is the AACPlayer which uses AACDecoder to decode AAC stream into PCM samples.
 * Uses java.nio.* API.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;

import android.content.Context;
import android.util.Log;

public class DirectAACPlayer implements PlayerCallback {

    private static final String LOG = "DirectAACPlayer";
    
    private boolean stopped;

    public DirectAACPlayer(Context context) {
  	
    }

    public void playAsync(final String url, final Decoder decoder) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    play(url, decoder);
                }
                catch (Exception e) {
                    Log.e( LOG, "playAsync():", e);

                    if (DirectAACPlayer.this != null) playerException( e );
                }
            }
        }).start();
    }


    public void play( String url, Decoder decoder) throws Exception {
        if (url.indexOf( ':' ) > 0) {
            URLConnection cn = new URL( url ).openConnection();
            cn.connect();

            play(Channels.newChannel( cn.getInputStream()), decoder); 
        }
        else play(new FileInputStream( url ).getChannel(), decoder);
    }


    public void play( ReadableByteChannel rbc, Decoder decoder) throws Exception {
        if (DirectAACPlayer.this != null) playerStarted();

        if (rbc instanceof SelectableChannel) {
            try {
                ((SelectableChannel) rbc).configureBlocking( true );
            }
            catch (IOException e) {
                Log.e( LOG, "play(): cannot adjust blocking", e );
            }
        }
        else {
            Log.w( LOG, "play(): not selectable channel: " + rbc );
        }

        DirectBufferReader reader = new DirectBufferReader( 2048, 1024, 1024, rbc );
        new Thread( reader ).start();

        try { Thread.sleep(500);} catch (InterruptedException e) {}

        stopped = false;

        DirectPCMFeed pcmfeed = null;

        // profiling info
        long profMs = 0;
        int profCount = 0;

        try {
            ByteBuffer inputBuffer = reader.next();
            Decoder.Info info = decoder.start( inputBuffer );

            Log.d( LOG, "play(): samplerate=" + info.getSampleRate() + ", channels=" + info.getChannels());

            if (info.getChannels() > 2) {
                throw new RuntimeException("Too many channels detected: " + info.getChannels());
            }

            // 3 buffers for result samples:
            //   - one is used by decoder
            //   - one is used by the PCMFeeder
            //   - one is enqueued / passed to PCMFeeder - non-blocking op
            int samplesCapacity = info.getChannels() * info.getSampleRate() * 2;

            ByteBuffer[] bbuffers = new ByteBuffer[3];
            ShortBuffer[] buffers = new ShortBuffer[3];

            for (int i=0; i < buffers.length; i++) {
                bbuffers[i] = ByteBuffer.allocateDirect( 2*samplesCapacity );
                buffers[i] = bbuffers[i].asShortBuffer();
            }

            ByteBuffer outputBBuffer = bbuffers[0]; 
            ShortBuffer outputBuffer = buffers[0]; 

            int samplespoolindex = 0;

            pcmfeed = new DirectPCMFeed( info.getSampleRate(), info.getChannels(), outputBuffer.capacity());
            new Thread(pcmfeed).start();

            do {
                long tsStart = System.currentTimeMillis();

                /*
                outputBBuffer.put(0, (byte) 0xAB ); 
                outputBBuffer.put(1, (byte) 0xCD ); 

                if (inputBuffer.position()+1 < inputBuffer.limit())
                    Log.d( LOG,  "play(): BEFORE in[0]="
                        + Integer.toHexString( inputBuffer.get( inputBuffer.position()))
                        + ", in[1]=" + Integer.toHexString( inputBuffer.get( inputBuffer.position()+1)));

                Log.d( LOG,  "play(): BEFORE out_b[0]="
                    + Integer.toHexString(outputBBuffer.get(0))
                    + ", out_s[0]=" + Integer.toHexString( outputBuffer.get(0)));
                */

                int nsamp = decoder.decode( inputBuffer, outputBBuffer );

                /*
                if (outputBBuffer.limit() != 0)
                    Log.d( LOG,  "play(): AFTER out_b[0]="
                        + Integer.toHexString(outputBBuffer.get(0))
                        + ", out_s[0]=" + Integer.toHexString( outputBuffer.get(0)));
                */

                profMs += System.currentTimeMillis() - tsStart;
                profCount++;

                Log.d( LOG, "play(): decoded " + nsamp + " samples" );

                if (stopped) break;

                outputBuffer.position(0);
                outputBuffer.limit( nsamp > 0 ? nsamp : 0);

                pcmfeed.feed( outputBuffer );
                if (stopped) break;

                outputBuffer = buffers[ ++samplespoolindex % 3 ];
                outputBuffer.clear();

                outputBBuffer = bbuffers[ samplespoolindex % 3 ];
                outputBBuffer.clear();

                inputBuffer = reader.next();

                Log.d( LOG, "play(): yield, sleeping...");
                try { Thread.sleep( 50 ); } catch (InterruptedException e) {}
            } while (inputBuffer != null && !stopped);
        }
        finally {
            stopped = true;

            if (pcmfeed != null) pcmfeed.stop();
            decoder.stop();

            if (profCount > 0) Log.i( LOG, "play(): average decoding time: " + profMs / profCount + " ms");

            if (DirectAACPlayer.this != null) playerStopped();
        }
    }


    public void stop() {
        stopped = true;
    }


	@Override
	public void playerStarted() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerDataRead(int bytes) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerStopped() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void playerException(Throwable t) {
		// TODO Auto-generated method stub
		
	}

}

