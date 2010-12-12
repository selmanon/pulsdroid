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

import java.util.List;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.AwayEvent;
import jerklib.events.CtcpEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.InviteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.KickEvent;
import jerklib.events.MessageEvent;
import jerklib.events.NickChangeEvent;
import jerklib.events.NickInUseEvent;
import jerklib.events.NickListEvent;
import jerklib.events.NoticeEvent;
import jerklib.events.NumericErrorEvent;
import jerklib.events.PartEvent;
import jerklib.events.QuitEvent;
import jerklib.events.TopicEvent;
import jerklib.events.WhoEvent;
import jerklib.events.WhoisEvent;
import jerklib.events.WhowasEvent;
import jerklib.listeners.IRCEventListener;
import android.os.Handler;
import android.os.Message;

public class IrcSession implements IRCEventListener, Runnable {
	
    private ConnectionManager mManager;
    private Handler mHandler;
    private Session mSession;
    private String mHost;
    private int mPort;
    private String mNick;
    private String mChan;

    public IrcSession (String host, int port, String nick, String pass, String chan, String secret, Handler handler) {
        mHost = host;
        mPort = port;
        mNick = nick;
        mChan = chan;
        mHandler = handler;
    }

    public void run () {
        /**
         *  Create IRC session
         */
        process("*** Initializing...");
        mManager = new ConnectionManager(new Profile(mNick));
        process("*** Connecting...");
        mSession = mManager.requestConnection(mHost, mPort);
        mSession.addIRCEventListener(this);
    }

    public void receiveEvent(IRCEvent e)
    {
        if (e.getType() == Type.CONNECT_COMPLETE)
        {
            /*if (mPass.length() > 0) {
                process("*** Identifying...");
                e.getSession().sayPrivate("NickServ", "IDENTIFY " + mPass);
            }*/
            process("*** Joining...");
            /*if (mSecret != null)
                e.getSession().join(mChan, mSecret);
            else*/
                e.getSession().join(mChan);
        }
        else if (e.getType() == Type.CHANNEL_MESSAGE)
        {
            MessageEvent me = (MessageEvent) e;
            process(me.getNick() + ": " + me.getMessage());
        }
        else if (e.getType() == Type.PRIVATE_MESSAGE)
        {
            MessageEvent me = (MessageEvent) e;
            process("<" + me.getNick() + "> " + me.getMessage());
        }
        else if (e.getType() == Type.AWAY_EVENT)
        {
            AwayEvent ae = (AwayEvent) e;
            process(ae.getNick() + " is away: " + ae.getAwayMessage());
        }
        else if (e.getType() == Type.CONNECTION_LOST)
        {
            //ConnectionLostEvent cle = (ConnectionLostEvent) e;
            process("*** Connection Lost!");
        }
        else if (e.getType() == Type.CTCP_EVENT)
        {
            CtcpEvent ctce = (CtcpEvent) e;
            String message = ctce.getMessage();
            if (message.startsWith("ACTION", 1)) {
                String msg = message.substring(8, message.length() - 1);
                process("***" + ctce.getNick() + " " + msg);
            }
            else if (message.startsWith("VERSION", 1)) {
            }
            else
                process("*** CTCP from " + ctce.getNick() + ": " + ctce.getMessage());
        }
        else if (e.getType() == Type.INVITE_EVENT)
        {
            InviteEvent ie = (InviteEvent) e;
            process("*** Invitation from " + ie.getNick() + " to channel: " + ie.getChannelName() + ".");
        }
        else if (e.getType() == Type.KICK_EVENT)
        {
            KickEvent ke = (KickEvent) e;
            process("*** " + ke.getWho() + " kicked from channel: " + ke.getChannel() + ".");
        }
        else if (e.getType() == Type.MOTD)
        {
        	//MotdEvent me = (MotdEvent) e;
        	//process(me.getMotdLine());
        }
        else if (e.getType() == Type.JOIN_COMPLETE)
        {
            JoinCompleteEvent jce = (JoinCompleteEvent) e;
            process ("*** Joined channel: " + jce.getChannel().getName() + ".");
        }
        else if (e.getType() == Type.TOPIC)
        {
            TopicEvent tpe = (TopicEvent) e;
            process("*** Topic is: " + tpe.getTopic());
        }
        else if (e.getType() == Type.NICK_LIST_EVENT)
        {
            NickListEvent nle = (NickListEvent) e;
            process("*** Nicks are: " + nle.getNicks());
        }
        else if (e.getType() == Type.NICK_CHANGE)
        {
            NickChangeEvent nce = (NickChangeEvent) e;
            process("*** " + nce.getOldNick() + " is now known as: " + nce.getNewNick() + ".");
        }
        else if (e.getType() == Type.NICK_IN_USE)
        {
            NickInUseEvent nue = (NickInUseEvent) e;
            process("*** Nick already in use: " + nue.getInUseNick() + ".");
        }
        else if (e.getType() == Type.NOTICE)
        {
            NoticeEvent noe = (NoticeEvent) e;
            if (noe.byWho().length() > 0)
                process("*** Notice by " + noe.byWho() + ":\n" + noe.getNoticeMessage());
            else
                process(noe.getNoticeMessage());
        }
        else if (e.getType() == Type.WHO_EVENT)
        {
            WhoEvent we = (WhoEvent) e;
            process("*** " + we.getNick() + " is " + we.getRealName() + " (" + we.getUserName() + "@" + we.getHostName() + ").");
        }
        else if (e.getType() == Type.WHOIS_EVENT)
        {
            WhoisEvent we = (WhoisEvent) e;
            process("*** " + we.getNick() + " is " + we.getRealName() + "( " + we.getUser() + "@" + we.getHost() + ").");
        }
        else if (e.getType() == Type.WHOWAS_EVENT)
        {
            WhowasEvent we = (WhowasEvent) e;
            process(we.getNick() + " was " + we.getRealName() + " (" + we.getUserName() + "@" + we.getHostName() + ")");
        }
        else if (e.getType() == Type.JOIN)
        {
            JoinEvent je = (JoinEvent) e;
            process("*** " + je.getNick() + " joined " + je.getChannelName() + ".");
        }
        else if (e.getType() == Type.PART)
        {
            PartEvent pe = (PartEvent) e;
            if (pe.getPartMessage().length() > 0)
                process("*** " + pe.getWho() + " left " + pe.getChannelName() + ": " + pe.getPartMessage());
            else
                process("*** " + pe.getWho() + " left " + pe.getChannelName() + ".");
        }
        else if (e.getType() == Type.QUIT)
        {
            QuitEvent qe = (QuitEvent) e;
            if (qe.getQuitMessage().length() > 0)
                process("*** " + qe.getNick() + " quitted: " + qe.getQuitMessage());
            else
                process("*** " + qe.getNick() + " quitted.");
        }
        else if (e.getType() == Type.SERVER_INFORMATION)
        {
        }
        else if (e.getType() == Type.SERVER_VERSION_EVENT)
        {
        }
        else if (e.getType() == Type.ERROR)
        {
            NumericErrorEvent ne = (NumericErrorEvent) e;
            process("*** Error: " + ne.getErrorMsg());
        }
        else if (e.getType() == Type.DEFAULT)
        {
        }
    }

    public void send(String message) {
        mSession.sayChannel(mSession.getChannel(mChan), message);
        process(mSession.getNick() + ": " + message);
    }

    public void sendprv(String nick, String message) {
        mSession.sayPrivate(nick, message);
    }

    public List<String> nickslist() {
        return mSession.getChannel(mChan).getNicks();
    }

    public void nicks() {
        String tmp = "*** Nicks are: [";
        List<String> nicks = mSession.getChannel(mChan).getNicks();
        for (String nick : nicks) {
            tmp = tmp.concat(nick + ", ");
        }
        String message = tmp.substring(0, tmp.length() - 2);
        message = message.concat("]");
        process(message);
    }

    public void me(String action) {
        mSession.getChannel(mChan).action(action);
        process("***" + mSession.getNick() + " " + action);
    }

    public void setnick(String nick) {
        mSession.changeNick(nick);
    }

    public void gettopic() {
        String topic = mSession.getChannel(mChan).getTopic();
        process("*** Topic is: " + topic);
    }

    public void settopic(String topic) {
        mSession.getChannel(mChan).setTopic(topic);
        //process("*** Topic is: " + topic);
    }

    public void quit() {
        mManager.quit();
    }

    private void process (String text) {
        Message msg = mHandler.obtainMessage();
        msg.obj = text;
        mHandler.sendMessage(msg);
    }
}