package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.util.EventListener;

/** EventListener to be implemented by objects that want to
    get callbacks when MP3 tags are received.
 */
public interface TagParseListener extends EventListener {

    /** Called when a tag is found (parsed from the stream,
        received via UDP, etc.)
     */
    public void tagParsed (TagParseEvent tpe);

}
