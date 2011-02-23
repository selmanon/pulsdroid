package net.sourceforge.jicyshout.jicylib1.metadata;
/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** A tag representing an "interesting" response header
    from the http server.  Icecast servers tend to send
    a bunch of "x-audiocast" headers that give the stream URL,
    genre, the server's physical location, etc.
 */
public class HTTPHeaderTag extends MP3Tag
    implements StringableTag {

    public HTTPHeaderTag (String name, String value) {
        super (name, value);
    }

    public String getValueAsString () {
        return (String) value;
    }

}
