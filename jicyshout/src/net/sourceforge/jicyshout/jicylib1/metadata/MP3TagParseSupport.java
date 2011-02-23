package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

import java.util.ArrayList;


/**  
*/
public class MP3TagParseSupport extends Object {

    ArrayList tagParseListeners;

    /** trivial constructor, sets up listeners list.
     */
    public MP3TagParseSupport() {
        super();
        tagParseListeners = new ArrayList();
    }

    /** Adds a TagParseListener to be notified when a stream
        parses MP3Tags.
     */
    public void addTagParseListener (TagParseListener tpl) {
        tagParseListeners.add (tpl);
    }

    /** Removes a TagParseListener, so it won't be notified when
        a stream parses MP3Tags.
     */
    public void removeTagParseListener (TagParseListener tpl) {
        tagParseListeners.add (tpl);
    }

    /** Fires the given event to all registered listeners
     */
    public void fireTagParseEvent (TagParseEvent tpe) {
        for (int i=0; i<tagParseListeners.size(); i++) {
            TagParseListener l = (TagParseListener) tagParseListeners.get (i);
            l.tagParsed (tpe);
        }
    }

    public void fireTagParsed (Object source, MP3Tag tag) {
        fireTagParseEvent (new TagParseEvent(source, tag));
    }

}
