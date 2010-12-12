package com.pulsradio.pulsdroid;

/**
 * Copyright (C) 2010 <David SANCHEZ>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Sources : http://herewe.servebeer.com/clinet/
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class IrcDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_HOST = "host";
    public static final String KEY_PORT = "port";
    public static final String KEY_NICK = "nick";
    public static final String KEY_PASS = "pass";
    public static final String KEY_CHAN = "chan";
    public static final String KEY_SECRET = "secret";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "IrcDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table connection (_id integer primary key autoincrement, "
                    + "title text not null, host text, "
                    + "port integer, nick text, "
                    + "pass text, chan text, secret text);";

    private static final String DATABASE_NAME = "ircdata";
    private static final String DATABASE_TABLE = "connection";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS connection");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public IrcDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the connection database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public IrcDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new connection using the infos provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param ...
     * @return rowId or -1 if failed
     */
    public long createConnection(String title, String host, String port, String nick, String pass, String chan, String secret) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_HOST, host);
        initialValues.put(KEY_PORT, port);
        initialValues.put(KEY_NICK, nick);
        initialValues.put(KEY_PASS, pass);
        initialValues.put(KEY_CHAN, chan);
        initialValues.put(KEY_SECRET, secret);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the connection with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteConnection(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all connections in the database
     * 
     * @return Cursor over all connections
     */
    public Cursor fetchAllConnections() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_HOST, KEY_PORT, KEY_NICK, KEY_PASS, KEY_CHAN, KEY_SECRET}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the connection that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching connection, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchConnection(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_TITLE, KEY_HOST, KEY_PORT, KEY_NICK, KEY_PASS, KEY_CHAN, KEY_SECRET}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the connection using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param ... values
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateConnection(long rowId, String title, String host, String port, String nick, String pass, String chan, String secret) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_HOST, host);
        args.put(KEY_PORT, port);
        args.put(KEY_NICK, nick);
        args.put(KEY_PASS, pass);
        args.put(KEY_CHAN, chan);
        args.put(KEY_SECRET, secret);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
