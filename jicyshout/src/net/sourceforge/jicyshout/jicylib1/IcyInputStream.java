package net.sourceforge.jicyshout.jicylib1;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

import net.sourceforge.jicyshout.jicylib1.metadata.IcyTag;
import net.sourceforge.jicyshout.jicylib1.metadata.MP3Tag;
import net.sourceforge.jicyshout.jicylib1.metadata.MP3TagParseSupport;

public class IcyInputStream extends BufferedInputStream implements Runnable {

    private MP3TagParseSupport tagParseSupport;
    
    private String name;
    public String value;


    /**
     * inline tags are delimited by ';', also filter out
     * null bytes
     */
    protected static final String INLINE_TAG_SEPARATORS = ";\u0000";

    /**
     * looks like icy streams start start with
     * ICY 200 OK\r\n
     * then the tags are like
     * icy-notice1:<BR>This stream requires <a href="http://www.winamp.com/">Winamp</a><BR>\r\n
     * icy-notice2:SHOUTcast Distributed Network Audio Server/win32 v1.8.2<BR>\r\n
     * icy-name:Core-upt Radio\r\n
     * icy-genre:Punk Ska Emo\r\n
     * icy-url:http://www.core-uptrecords.com\r\n
     * icy-pub:1\r\n
     * icy-metaint:8192\r\n
     * icy-br:56\r\n
     * \r\n (signifies end of headers)
     * we only get icy-metaint if the http request that created
     * this stream sent the header "icy-metadata:1"
     * //
     * in in-line metadata, we read a byte that tells us how
     * many 16-byte blocks there are (presumably, we still use
     * \r\n for the separator... the block is padded out with
     * 0x00's that we can ignore)
     * 
     * // when server is full/down/etc, we get the following for
     * // one of the notice lines:
     * icy-notice2:This server has reached its user limit<BR>
     * or
     * icy-notice2:The resource requested is currently unavailable<BR>
     */


    /**
     * Tags that have been discovered in the stream.
     */
    HashMap tags;

    /**
     * Buffer for readCRLF line... note this limits lines to
     * 1024 chars (I've read that WinAmp barfs at 128, so
     * this is generous)
     */
    protected byte[] crlfBuffer = new byte[1024];

    /**
     * value of the "metaint" tag, which tells us how many bytes
     * of real data are between the metadata tags.  if -1, this stream
     * does not have metadata after the header.
     */
    protected int metaint = -1;

    /**
     * how many bytes of real data remain before the next
     * block of metadata.  Only meaningful if metaint != -1.
     */
    protected int bytesUntilNextMetadata = -1;

    /**
     * Reads the initial headers of the stream and adds
     * tags appropriatly.  Gets set up to find, read, 
     * and strip blocks of in-line metadata if the
     * <code>icy-metaint</code> header is found.
     */ 
    public IcyInputStream (InputStream in) throws IOException {
        super (in);
        tags = new HashMap();
        tagParseSupport = new MP3TagParseSupport();

        /**
         * read the initial tags here, including the metaint
         * and set the counter for how far we read until
         * the next metadata block (if any).
         */
        readInitialHeaders();
        IcyTag metaIntTag = (IcyTag) getTag ("icy-metaint");
        if (metaIntTag != null) {
            String metaIntString = metaIntTag.getValueAsString();
            try {
                metaint = Integer.parseInt (metaIntString);
                bytesUntilNextMetadata = metaint;
            } catch (NumberFormatException nfe) {}
        }
    }


    /**
     * Assuming we're at the top of the stream, read lines one
     * by one until we hit a completely blank \r\n.  Parse the 
     * data as IcyTags.
     */
    protected void readInitialHeaders() throws IOException {
        String line = null;
        while (! ((line = readCRLFLine()).equals(""))) {
            int colonIndex = line.indexOf (':');
            // does it have a ':' separator
            if (colonIndex == -1)
                continue;
            IcyTag tag = new IcyTag (line.substring (0, colonIndex), line.substring (colonIndex+1));
            setTag(tag);
        }

    }

    /**
     * Read everything up to the next CRLF, return it as 
     * a String.
     */
    protected String readCRLFLine() throws IOException {
        int i=0;
        for (; i<crlfBuffer.length; i++) {
            byte aByte = (byte) read();
            if (aByte=='\r') {
                // possible end of line
                byte anotherByte = (byte) read();
                i++; // since we read again
                if (anotherByte == '\n') {
                    break; // break out of while
                } else {
                    // oops, not end of line - put these in array
                    crlfBuffer[i-1] = aByte;
                    crlfBuffer[i] = anotherByte;
                }
            } else {
                // if not \r
                crlfBuffer[i] = aByte;
            }
        } // for
        // get the string from the byte[].  i is 1 too high because of
        // read-ahead in crlf block
        return new String (crlfBuffer, 0, i-1);
    }

    /**
     * Reads and returns a single byte.
     * If the next byte is a metadata block, then that
     * block is read, stripped, and parsed before reading
     * and returning the first byte after the metadata block.
     */
    public int read() throws IOException {
        if (bytesUntilNextMetadata > 0) {
            bytesUntilNextMetadata--;
            return super.read();
        }
        else if (bytesUntilNextMetadata == 0) {
            // we need to read next metadata block
            readMetadata();
            bytesUntilNextMetadata = metaint - 1;
            // -1 because we read byte on next line
            return super.read();
        } else {
            // no metadata in this stream
            return super.read();
        }
    }


    /**
     * Reads a block of bytes.  If the next byte is known
     * to be a block of metadata, then that is read, parsed,
     * and stripped, and then a block of bytes is read and
     * returned.
     * Otherwise, it may read up to but
     * not into the next metadata block if
     * <code>bytesUntilNextMetadata &lt; length</code>
     */
    public int read (byte[] buf, int offset, int length) throws IOException {
        // if not on metadata, do the usual read so long as we
        // don't read past metadata
        if (bytesUntilNextMetadata > 0) {
            int adjLength =
                Math.min (length, bytesUntilNextMetadata);
            int got =
                super.read (buf, offset, adjLength);
            bytesUntilNextMetadata-=got;
            return got;
        } else if (bytesUntilNextMetadata == 0) {
            // read/parse the metadata
            readMetadata();
            // now as above, except that we reset
            // bytesUntilNextMetadata differently
            int adjLength =
                Math.min (length, bytesUntilNextMetadata);
            int got =
                super.read (buf, offset, adjLength);
            bytesUntilNextMetadata = metaint - got;
            return got;
        } else {
            // not even reading metadata
            return super.read (buf, offset, length);
        }

    }

    /**
     * trivial <code>return read (buf, 0, buf.length)</code>
     */
    public int read (byte[] buf) throws IOException {
        return read (buf, 0, buf.length);
    }

    /**
     * Read the next segment of metadata.  The stream <b>must</b>
     * be right on the segment, ie, the next byte to read is
     * the metadata block count.  The metadata is parsed and
     * new tags are added with addTag(), which fires events
     */
    protected void readMetadata() throws IOException {
        int blockCount = super.read();
        int byteCount = blockCount * 16; // 16 bytes per block
        if (byteCount < 0)
            return; // WTF?!
        byte[] metadataBlock = new byte[byteCount];
        int index = 0;
        // build an array of this metadata
        while (byteCount > 0) {
            int bytesRead =
                super.read (metadataBlock, index, byteCount);
            index += bytesRead;
            byteCount -= bytesRead;
        }

        // now parse it
        if (blockCount > 0) {
        	parseInlineIcyTags (metadataBlock);
        }
    } // readMetadata

    /**
     * Parse metadata from an in-stream "block" of bytes, add
     * a tag for each one.
     * <p>
     * Hilariously, the inline data format is totally different
     * than the top-of-stream header.  For example, here's a
     * block I saw on "Final Fantasy Radio":
     * <pre>
     * StreamTitle='Final Fantasy 8 - Nobuo Uematsu - Blue Fields';StreamUrl='';
     * </pre>
     * In other words:
     * <ol>
     * <li>Tags are delimited by semicolons
     * <li>Keys/values are delimited by equals-signs
     * <li>Values are wrapped in single-quotes
     * <li>Key names are in SentenceCase, not lowercase-dashed
     * </ol>
     */
    protected void parseInlineIcyTags (byte[] tagBlock) {
        String blockString = new String (tagBlock);
        StringTokenizer izer = new StringTokenizer (blockString, INLINE_TAG_SEPARATORS);
        String tagString = izer.nextToken();
        int separatorIdx = tagString.indexOf ('=');
        int valueStartIdx = (tagString.charAt(separatorIdx+1) == '\'') ? separatorIdx + 2 : separatorIdx + 1;
        int valueEndIdx = (tagString.charAt(tagString.length()-1)) == '\'' ? tagString.length() - 1 : tagString.length();

        name = tagString.substring (0, separatorIdx);
        value = tagString.substring (valueStartIdx, valueEndIdx);
        System.out.println ("StreamTitle : " + value);
        IcyTag tag = new IcyTag (name, value);
        setTag(tag);
    }
    
    public String getValue(){
    	return this.value;
    }
    
    /**
     * adds the tag to the HashMap of tags we have encountered
     * either in-stream or as headers, replacing any previous
     * tag with this name.
     */
    protected void setTag(IcyTag tag) {
        tags.put (tag.getName(), tag);
        // fire this as an event too
        tagParseSupport.fireTagParsed (this, tag);
    }

    /**
     * Get the named tag from the HashMap of headers and 
     * in-line tags.  Null if no such tag has been encountered.
     */
    public MP3Tag getTag (String tagName) {
        return (MP3Tag) tags.get (tagName); // --> This
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public String IcyTagged(String name) {
		return ((IcyTag) getTag (name)).toString();
    }
    
    /**
     * This method is called when the thread runs
     */
    public void run() {

    }

}