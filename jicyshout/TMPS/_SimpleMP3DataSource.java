package net.sourceforge.jicyshout.jicylib1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

import net.sourceforge.jicyshout.jicylib1.metadata.MP3Tag;
import net.sourceforge.jicyshout.jicylib1.metadata.MP3TagParseSupport;
import net.sourceforge.jicyshout.jicylib1.metadata.TagParseEvent;
import net.sourceforge.jicyshout.jicylib1.metadata.TagParseListener;


public class _SimpleMP3DataSource extends PullDataSource implements TagParseListener {
    //implements TagParseListener, MP3MetadataParser {

    protected MediaLocator myML;
    protected InputStream httpStream;
    protected boolean parseStreamMetadata;
    protected SeekableInputStream seekStream;
    protected URLConnection urlConnection;
    protected PullSourceStream[] sourceStreams;
    protected Object[] EMPTY_CONTROL_ARRAY = {};
    protected int preferredUDPPort = -1;
    protected UDPMetadataListener iceListener;
    protected MP3TagParseSupport tagParseSupport;



    public _SimpleMP3DataSource (MediaLocator ml,
                                boolean parseStreamMetadata)
        throws MalformedURLException {
        super ();
        myML = ml;
        this.parseStreamMetadata = parseStreamMetadata;
        tagParseSupport = new MP3TagParseSupport();

    }

    public void connect()
        throws IOException {
        try {
            //System.out.println ("top of connect()");
            URL url = myML.getURL();
            // get the connection, maybe request icy metadata
            urlConnection = url.openConnection();
            //System.out.println ("opened connection " + urlConnection.getClass().getName());
            // only send this if we intend to get metadata
            if (parseStreamMetadata) {
                urlConnection.setRequestProperty ("Icy-Metadata", "1");
                urlConnection.setRequestProperty ("x-audiocast-udpport", Integer.toString(6000));
            }

            // get a metadata-parsing stream, if type can be
            // determined from first 1KB
            httpStream = urlConnection.getInputStream();
            InputStream mp3Stream; 
            if (parseStreamMetadata)
               mp3Stream =  MP3InputStreamFactory.getInstance().getMP3InputStreamFor (httpStream, 1024);
            else
                mp3Stream = httpStream;
            // if the stream is a metadata parser, and if we
            // want to get in-stream metadata, get all
            // tags currently parsed and listen for more
            /*if (parseStreamMetadata &&
                (mp3Stream instanceof MP3MetadataParser)) {
                MP3MetadataParser parser = (MP3MetadataParser) mp3Stream;
                parser.addTagParseListener (this);
            }*/
            // make the stream seekable,
            // so jmf mp3 parser can handle it
            seekStream = new SeekableInputStream (mp3Stream);
            //System.out.println ("got input stream");
            sourceStreams = new PullSourceStream[1];
            sourceStreams[0] = seekStream;
            //System.out.println ("connect done, stream is " + sourceStreams[0]);
        } catch (MalformedURLException murle) {
            throw new IOException ("Malformed URL: " +
                                   murle.getMessage());
        }
    }

    public void disconnect() {
    }

    public void start() {

    }

    public void stop() {
    }

    public PullSourceStream[] getStreams() {
        return sourceStreams;
    }

    public void tagParsed (TagParseEvent evt) {
        System.out.println (evt.getTag());
    }

	/*@Override
	public MP3Tag[] getTags() {
		// TODO Auto-generated method stub
		return null;
	}*/

	/*@Override
	public void addTagParseListener(TagParseListener tpl) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeTagParseListener(TagParseListener tpl) {
		// TODO Auto-generated method stub
	}*/

	@Override
	public Object getControl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getDuration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}
}
