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

import java.lang.String;
import java.util.LinkedList;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ScrollView;
import android.view.KeyEvent;
import android.text.SpannableString;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.util.Linkify;

public class ChanIRC extends Activity {

    private ScrollView mScrollView;
    private TextView mTextView;
    private EditText mEditText;
    private Button mSendButton;
    private String mHost;
    private String mNick;
    private String mPass;
    private String mChan;
    private String mSecret;
    private Handler mHandler;
    private IrcSession mIrcSession;
    private WifiLock mWifilock;
    
    /**
	 * Déclaration de l'objet inflater
	 * C'est un objet de la classe MenuInflater
	 * Cela permet d'instancier les menu au format XML en menu objet
	 */
	private MenuInflater inflater;
	
	private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chan_irc);

        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mTextView = (TextView) findViewById(R.id.textview);
        mEditText = (EditText) findViewById(R.id.textedit);
        mSendButton = (Button) findViewById(R.id.send);

        Bundle extras = getIntent().getExtras();
        mHost = "irc.pulsradio.com";
        mNick = extras.getString("nick");
        mPass = "";
        mChan = "#Puls";
        mSecret = "";

        /**
         *  Create text handler
         */
        mHandler = new Handler() {
                @Override public void handleMessage(Message msg)
                {
                    CharSequence text = (CharSequence) msg.obj.toString();
                    SpannableString str = colorText(text);
                    mTextView.append(str);
                    Linkify.addLinks(mTextView, Linkify.ALL);
                    mTextView.append("\n");
                    mScrollView.scrollTo(0, mTextView.getHeight());
                }
            };
 
        /**
         * Create send button callback
         */
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onSend();
            }
        });

        /**
         * Create 'return' key callback
         */
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                /* 'Enter' pressed */
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onSend();
                    return true;
                /* 'Tab' pressed */
                } else if ((event.isAltPressed() && keyCode == KeyEvent.KEYCODE_Q && event.getAction() == KeyEvent.ACTION_DOWN) || keyCode == KeyEvent.KEYCODE_TAB && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return onTab();
                } else {
                    return false;
                }
            }
        });

        /**
         * lock wifi if enabled
         */
        WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
        mWifilock = wifi.createWifiLock("clinet");
        if (wifi.isWifiEnabled()) {
            mWifilock.acquire();
        }
        
        /**
         * Connection mIRC
         */
        mIrcSession = new IrcSession(mHost, 6667, mNick, mPass, mChan, mSecret, mHandler);
        thread = new Thread(mIrcSession);
        thread.start();

}

    /**
     * Called when the tabulation key is pressed
     */
    public boolean onTab() {
        LinkedList<String> available = new LinkedList<String>();
        String text = mEditText.getText().toString();
        String b = text.substring(text.lastIndexOf(' ') + 1, text.length());
        for (String nick : mIrcSession.nickslist()) {
            if (nick.startsWith(b)) {
                available.add(nick);
            }
        }
        if (available.size() == 0) /* nothing to do */
            return false;
        else if (available.size() == 1) {
            /**
             * complete nickname
             */
            mEditText.append(available.getFirst().substring(b.length(), available.getFirst().length()) + ": ");
            return true;
        } else {
            String display = new String("*** ");
            for (String nick : available) {
                display = display + nick + " ";
            }
            SpannableString str = colorText(display);
            mTextView.append(str);
            mTextView.append("\n");
            mScrollView.scrollTo(0, mTextView.getHeight());

            String subnick = "";
            boolean found = false;
            for (int i = 0; !found; i++) {
                for (String nick : available) {
                    if (nick.length() < i || available.getFirst().charAt(i) != nick.charAt(i)) {
                        found = true; /* no more found */
                        break;
                    }
                    else {
                        subnick = nick.substring(0, i);
                    }
                }
            }
            mEditText.append(subnick.substring(b.length(), subnick.length()));
            return true;
        }
    }

    /**
     * Called when send button is clicked or whan 'return' key is typed
     */
    public void onSend() {
        String text = mEditText.getText().toString();
        if (text.length() > 0) {
            if (text.startsWith("/msg ")) {
                /* private message */
                int b = text.indexOf(' ') + 1;
                String temp = text.substring(b);
                int e = temp.indexOf(' ') + 1;
                String nick = text.substring(b, b + e);
                String message = text.substring(b + e, text.length());
                mIrcSession.sendprv(nick, message);
            }
            else if (text.startsWith("/me ")) {
                int b = text.indexOf(' ') + 1;
                String message = text.substring(b, text.length());
                mIrcSession.me(message);
            }
            else if (text.startsWith("/nicks")) {
                mIrcSession.nicks();
            }
            else if (text.startsWith("/nick ")) {
                int b = text.indexOf(' ') + 1;
                String nick = text.substring(b,text.length());
                mIrcSession.setnick(nick);
            }
            else if (text.startsWith("/topic") && !text.startsWith("/topic ")) {
                mIrcSession.gettopic();
            }
            else if (text.startsWith("/topic ")) {
                int b = text.indexOf(' ') + 1;
                String topic = text.substring(b,text.length());
                mIrcSession.settopic(topic);
            }
            else {
                mIrcSession.send(text);
            }
        }
        mEditText.setText("");
    }

    /**
     * called to colorize IRC messages
     */
    private SpannableString colorText(CharSequence text) {
        SpannableString str = new SpannableString(text);
        int color;
        /* topic color */
        if (text.toString().startsWith("*** Topic is: "))
            color = Color.YELLOW;
        /* nicks list color */
        else if (text.toString().startsWith("*** Nicks are: "))
            color = Color.GREEN;
        /* action color */
        else if (text.toString().startsWith("***"))
            color = Color.LTGRAY;
        /* cite color */
        else if (!text.toString().startsWith(mNick) && text.toString().contains(mNick)) {
            color = Color.CYAN;
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(200);
        }
        /* privmsg color */
        else if (text.toString().startsWith("<"))
        {
            color = Color.WHITE;
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(200);
        }
        /* default color */
        else
            color = Color.WHITE;

        str.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
        return str;
    }
    
    /**
     * Création du menu
     * On spécifie le fichier xml utilisé pour le menu ici, (menu.xml)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	inflater = getMenuInflater();
    	inflater.inflate(R.menu.menuirc, menu);
    	return true;
    }
    
    /**
     * Création des options du menu
     * Dès que l'utilisateur clic sur le bouton
     * alors, il sera déconnecté du chan IRC
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	    	case R.id.quit:
	    		mIrcSession.quit();
	    		onDestroy();
	    		return true;
    	}
    	return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
 
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        mIrcSession.quit();
        if (mWifilock.isHeld())
            mWifilock.release();
        super.onDestroy();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

}