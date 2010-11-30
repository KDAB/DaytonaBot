/*
    This file is part of PutBot.

    Copyright (c) 2010 Frank Osterfeld <frank.osterfeld@kdab.com>

    PutBot is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    PutBot is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.kdab.restbot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class JabberBot implements Runnable, ChatManagerListener, MessageListener {
    public JabberBot( BlockingQueue<Message> in, Account account, String nick, Vector<String> roomsToJoin ) {
        m_in = in;
        m_account = account;
        m_roomsToJoin = roomsToJoin;
        m_nick = nick;
        m_rooms = new HashMap<String, MultiUserChat>();
    }

    public void run() {
        try {
            login();
            joinRooms();
        } catch ( XMPPException e ) {
            System.err.println( e );
            // TODO how to report?
        }
        try {
            while ( true ) {
                Message msg = m_in.take();
                try {
                    send( msg );
                } catch ( XMPPException e ) {
                    System.err.println( e );
                    // TODO how to report?
                }
            }
        } catch ( InterruptedException e ) {
            logout();
            Thread.currentThread().interrupt();
        }
    }

    private void send( Message msg ) throws XMPPException {
        for ( Message.Receiver i : msg.receivers() ) {
            final String rec = i.receiver;
            assert( rec != null );
            assert( !rec.isEmpty() );

            if ( i.type == Message.ReceiverType.User ) {
                Chat chat = m_connection.getChatManager().createChat( rec, null );
                chat.addMessageListener( this );
                chat.sendMessage( msg.text() );
            } else {
                MultiUserChat c = m_rooms.get( rec );
                if ( c != null ) {
                    c.sendMessage( msg.text() );
                } else {
                    // report?
                }
            }
        }
    }

    private void login() throws XMPPException {
        assert( m_connection == null );
        final int magic = new Random().nextInt( 1000 );
        final ConnectionConfiguration connconf = new ConnectionConfiguration( m_account.server(), m_account
                .port() );
        connconf.setSecurityMode( SecurityMode.required );
        connconf.setSendPresence( true );
        connconf.setReconnectionAllowed( true );

        try {
            m_connection = new XMPPConnection( connconf );
            m_connection.connect();
            m_connection.login( m_account.user(), m_account.password(), "RestBot" + magic );
            m_connection.getChatManager().addChatListener(  this );
        } catch ( XMPPException e ) {
            m_connection.disconnect();
            m_connection = null;
            throw e;
        }
    }

    private void joinRooms() throws XMPPException {
        for ( String i : m_roomsToJoin ) {
            MultiUserChat c = new MultiUserChat( m_connection, i );
            c.join( m_nick );
            c.changeAvailabilityStatus( "Yo.", Presence.Mode.available );
            m_rooms.put( i, c );
        }
    }

    private void logout() {
        m_connection.disconnect();
    }

    private BlockingQueue<Message> m_in;
    private Account m_account;
    private Vector<String> m_roomsToJoin;
    private String m_nick;
    private XMPPConnection m_connection;
    private Map<String, MultiUserChat> m_rooms;

    public void chatCreated( Chat chat, boolean arg1 ) {
        // TODO check arg1
        chat.addMessageListener(  this );
    }

    public void processMessage( Chat chat, org.jivesoftware.smack.packet.Message msg ) {
        final String from = msg.getFrom();
        final String body = msg.getBody();
    }
}
