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
 * @name AACDecoder.java
 * @version 1.0 - 2011/04/03
 * @autor David SANCHEZ
 * @source http://code.google.com/p/aacplayer-android/
 */

public final class AACDecoder {
    private static final String LOG = "AACDecoder";
    
    private native int nativeStart( byte[] buf, int off, int len, AACInfo aacInfo );

    private native int nativeDecode( int aacdw, byte[] buf, int off, int len, short[] samples, int outLen );

    private native void nativeStop( int aacdw );

    private static enum State { IDLE, RUNNING };

    private static boolean libLoaded = false;

    private int aacdw;
    private State state = State.IDLE;

    private AACDecoder() {
    	
    }


    /**
     * Creates a new decoder.
     */
    public static synchronized AACDecoder create() {
        if (!libLoaded) {
            System.loadLibrary( "AACDecoder" );

            libLoaded = true;
        }

        return new AACDecoder();
    }


    /**
     * Starts decoding AAC stream.
     */
    public AACInfo start( byte[] buf, int off, int len ) {
        if (state != State.IDLE) throw new IllegalStateException();
        
        AACInfo ret = new AACInfo();

        aacdw = nativeStart( buf, off, len, ret );

        state = State.RUNNING;

        return ret;
    }


    /**
     * Decodes AAC stream.
     * @return the number of samples produced (totally all channels = the length of the filled array)
     */
    public int decode( byte[] buf, int off, int len, short[] samples, int outLen ) {
        if (state != State.RUNNING) throw new IllegalStateException();

        return nativeDecode( aacdw, buf, off, len, samples, outLen );
    }


    /**
     * Stops the decoder and releases all resources.
     */
    public void stop() {
        if (aacdw != 0) {
            nativeStop( aacdw );
            aacdw = 0;
        }

        state = State.IDLE;
    }

    @Override
    protected void finalize() {
        try {
            stop();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
