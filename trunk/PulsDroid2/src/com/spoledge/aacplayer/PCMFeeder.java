package com.spoledge.aacplayer;

import android.media.AudioTrack;
import android.util.Log;

class PCMFeeder extends AACPlayer implements Runnable {
    AudioTrack atrack;
    short[] samples;
    int n;

    PCMFeeder( AudioTrack atrack ) {
        this.atrack = atrack;
        new Thread(this).start();
    }

    public synchronized void stop() {
        notify();
    }

    public synchronized void feed( short[] samples, int n ) {
        while (this.samples != null && !stopped) {
            try { wait(); } catch (InterruptedException e) {}
        }

        this.samples = samples;
        this.n = n;

        notify();
    }


    public void run() {
        while (!stopped) {
            short[] lsamples = null;
            int ln = 0;

            synchronized (this) {
                while (n == 0 && !stopped) {
                    try { wait(); } catch (InterruptedException e) {}
                }

                lsamples = samples;
                ln = n;

                samples = null;
                n = 0;
                notify();
            }

            if (stopped) break;

            int playedTotal = 0;

            do {
                if (playedTotal != 0) {
                    Log.d( LOG, "play(): too fast for playback, sleeping...");
                    try { Thread.sleep( 100 ); } catch (InterruptedException e) {}
                }

                Log.d( LOG, "play(): feeding PCM...");
                int played = atrack.write( lsamples, 0, ln );
                Log.d( LOG, "play(): PCM fed by " + played + " samples");

                if (played < 0) {
                    Log.e( LOG, "play(): error in playback feed: " + played );
                    stopped = true;
                    break;
                }

                playedTotal += played;
            } while (playedTotal < ln);
        }
    }
}