package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.util.EventObject;

/** Event to indicate that an MP3 tag was received through
    some means (parsed in stream, received via UDP, whatever)
    and converted into an MP3Tag object.
 */
public class TagParseEvent extends EventObject {

    protected MP3Tag tag;

    public TagParseEvent (Object source, MP3Tag tag) {
        super(source);
        this.tag = tag;
    }

    /** Get the tag that was parsed.
     */
    public MP3Tag getTag() {
    	return tag;
    }
    
}

