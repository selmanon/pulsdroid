package net.sourceforge.jicyshout.jicylib1.metadata;

public abstract class MP3Tag extends Object {

    protected String name;
    protected Object value;

    public MP3Tag (String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        //return getClass().getName() + " -- " + getName() + ":" + getValue().toString();
        return getValue().toString();
    }

}
