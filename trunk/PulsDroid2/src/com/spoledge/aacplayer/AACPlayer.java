/*
** AACPlayer - Freeware Advanced Audio (AAC) Player for Android
** Copyright (C) 2010 Spolecne s.r.o., http://www.spoledge.com
**  
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
** 
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
** 
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software 
** Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
**/
package com.spoledge.aacplayer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;


/**
 * This is the AACPlayer which uses AACDecoder to decode AAC stream into PCM samples.
 */
public class AACPlayer {

    public static enum Quality {
        HIGH_128( 128 ),
        LOW_32( 32 );

        public final int downloadBytesPerSec;
        public final int minHeaderBytes;
        public final int minStepBytes;

        Quality( int kbits ) {
            downloadBytesPerSec = kbits * 1000 / 8;
            minHeaderBytes = downloadBytesPerSec * 3;
            minStepBytes = downloadBytesPerSec;
        }
    }


    protected static final String LOG = "AACPlayer";


    ////////////////////////////////////////////////////////////////////////////
    // Attributes
    ////////////////////////////////////////////////////////////////////////////

    protected boolean stopped;


    ////////////////////////////////////////////////////////////////////////////
    // Constructors
    ////////////////////////////////////////////////////////////////////////////

    public AACPlayer() {
    }


    ////////////////////////////////////////////////////////////////////////////
    // Public
    ////////////////////////////////////////////////////////////////////////////

    public void playAsync( final String url, final Quality quality ) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    play( url, quality );
                }
                catch (Exception e) {
                    Log.e( LOG, "playAsync():", e);
                }
            }
        }).start();
    }


    public void play( String url, Quality quality ) throws Exception {
        if (url.indexOf( ':' ) > 0) {
            URLConnection cn = new URL( url ).openConnection();
            cn.connect();

            play( cn.getInputStream(), quality ); 
        }
        else play( new FileInputStream( url ), quality);
    }


    public void play( InputStream is, Quality quality ) throws Exception {
        stopped = false;

        if (quality == null) quality = Quality.HIGH_128;

        byte[] buf = new byte[ quality.minHeaderBytes ];
        int n;
        int total=0;
        int min = quality.minHeaderBytes;

        while ((n = is.read(buf, total, buf.length - total)) != -1 && total < min ) {
            total += n;
        }

        AACDecoder aacd = AACDecoder.create();
        PCMFeeder pcmfeed = null;

        // profiling info
        long profMs = 0;
        int profCount = 0;

        try {
            AACInfo info = aacd.start( buf, 0, total );

            Log.d( LOG, "play(): samplerate=" + info.getSamplerate() + ", channels=" + info.getChannels());

            if (info.getChannels() > 2) {
                throw new RuntimeException("Too many channels detected: " + info.getChannels());
            }

            // 3 buffers for result samples:
            //   - one is used by decoder
            //   - one is used by the PCMFeeder
            //   - one is enqueued / passed to PCMFeeder - non-blocking op
            short[] samples = new short[ info.getChannels() * info.getSamplerate() * 4 ];
            short[][] samplespool = new short[3][];
            samplespool[0] = samples;
            samplespool[1] = new short[ samples.length ];
            samplespool[2] = new short[ samples.length ];
            int samplespoolindex = 0;

            AudioTrack atrack = new AudioTrack(
                                    AudioManager.STREAM_MUSIC,
                                    info.getSamplerate(),
                                    info.getChannels() == 1 ?
                                        AudioFormat.CHANNEL_CONFIGURATION_MONO :
                                        AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                                    AudioFormat.ENCODING_PCM_16BIT,
                                    samples.length,
                                    AudioTrack.MODE_STREAM );

            min = quality.minStepBytes;

            pcmfeed = new PCMFeeder( atrack );

            atrack.play();

            do {
                long tsStart = System.currentTimeMillis();

                int nsamp = aacd.decode( buf, 0, total, samples, samples.length );

                profMs += System.currentTimeMillis() - tsStart;
                profCount++;

                Log.d( LOG, "play(): decoded " + nsamp + " samples" );

                if (stopped) break;

                pcmfeed.feed( samples, nsamp );
                if (stopped) break;

                samples = samplespool[ ++samplespoolindex % 3 ];

                total = 0;
                while ((n = is.read( buf, 0, buf.length - total)) != -1 && total < min ) {
                    total += n;
                }

                Log.d( LOG, "play(): yield, sleeping...");
                try { Thread.sleep( 100 ); } catch (InterruptedException e) {}
            } while (n != -1 && !stopped);
        }
        finally {
            stopped = true;

            if (pcmfeed != null) pcmfeed.stop();
            aacd.stop();

            if (profCount > 0) Log.i( LOG, "play(): average decoding time: " + profMs / profCount + " ms");
        }
    }


    public void stop() {
        stopped = true;
    }
}
