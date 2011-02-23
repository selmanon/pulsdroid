package net.sourceforge.jicyshout.jicylib1.metadata;

/*
  jicyshout relased under terms of the lesser GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCLGPL
 */

/** A tag parsed from an icecast tag. 
 */
public class IcyTag extends MP3Tag implements StringableTag {

    /** Create a new tag, from the parsed name and (String) value.
        It looks like all Icecast tags are Strings (safe to assume
        this going forward?)
     */
    public IcyTag (String name, String stringValue) {
        super (name, stringValue);
    }

    // so far as I know, all Icecast tags are strings
    public String getValueAsString() {    	
        return (String) getValue();
    }

}
