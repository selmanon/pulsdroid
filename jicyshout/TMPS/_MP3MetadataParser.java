package net.sourceforge.jicyshout.jicylib1.metadata;


/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** An object that fires off TagParseEvents as they are parsed
    from a stream, ServerSocket, or other metadata source
  */
public interface _MP3MetadataParser {

    /** Adds a TagParseListener to be notified when this object
        parses MP3Tags.
     */
    public void addTagParseListener (TagParseListener tpl);

    /** Removes a TagParseListener, so it won't be notified when
        this object parses MP3Tags.
     */
    public void removeTagParseListener (TagParseListener tpl);

    /** Get all tags (headers or in-stream) encountered thusfar.
        This is included in this otherwise Listener-like scheme
        because most standards are a mix of start-of-stream
        metadata tags (like the http headers or the stuff at the
        top of an ice stream) and inline data.  Implementations should
        hang onto all tags they parse and provide them with this
        call.  Callers should first use this call to get initial
        tags, then subscribe for events as the stream continues.
     */
    public MP3Tag[] getTags();



}
