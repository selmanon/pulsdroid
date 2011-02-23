package net.sourceforge.jicyshout.jicylib1;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


public class _MP3InputStreamFactory extends Object {

    private static MP3InputStreamFactory instance;

    public static final String ICY_MAGIC_STRING = "ICY 200 OK";

    private _MP3InputStreamFactory() { super(); }

    public static MP3InputStreamFactory getInstance() {
        if (instance == null)
            instance = new MP3InputStreamFactory();
        return instance;
    }


    protected InputStream getMP3InputStreamForImpl (InputStream in, int maxBytes) {
       
        InputStream returnedStream = in; // default - no wrapper
        try {
            PushbackInputStream pushy =
                new PushbackInputStream (in, maxBytes);

            // TODO: take a peek for an MPEG header and remember
            // its vital statistics (encoding type, freq, bitrate, etc.)

            // first, look to see if this is icy (shoutcast)
            // (note - isShoutcastStream must push back everything!)
            if (isShoutcastStream (pushy, maxBytes)) {
                //System.out.println ("*** stream is Icy");
                returnedStream = new IcyInputStream (pushy);
            }
        } catch (IOException ioe) {
            // barf -- we'll just get returnedStream
        }
        return returnedStream;
    }


    protected boolean isShoutcastStream (PushbackInputStream pushy, int maxBytes)
        throws IOException {
        // read bytes and immediately push them back
        byte[] buf = new byte[15];
        int bytesRead = pushy.read (buf, 0, buf.length);
        pushy.unread (buf, 0, bytesRead);
        // now, did we get ICY_MAGIC_STRING?
        String bufString = new String (buf, 0, bytesRead);
        // System.out.println ("top of stream is " + bufString);
        return (bufString.indexOf(ICY_MAGIC_STRING) != -1);
    }


    public static InputStream getMP3InputStreamFor (InputStream in, int maxBytes) {
        return getInstance().getMP3InputStreamForImpl (in, maxBytes);
    }

}
