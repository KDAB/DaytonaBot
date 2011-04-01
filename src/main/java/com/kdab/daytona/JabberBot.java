/*
    This file is part of Daytona.

    Copyright (c) 2010 Frank Osterfeld <frank.osterfeld@kdab.com>

    Daytona is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    Daytona is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.kdab.daytona;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class JabberBot implements Runnable, ChatManagerListener, MessageListener {
    public JabberBot( BlockingQueue<Message> in, Account account, String nick, Vector<String> admins, Vector<String> roomsToJoin, Logger logger ) {
        m_in = in;
        m_account = account;
        m_roomsToJoin = roomsToJoin;
        m_admins = admins;
        m_nick = nick;
        m_rooms = new HashMap<String, MultiUserChat>();
        m_logger = logger;
    }

    public void run() {
        try {
            login();
            joinRooms();
        } catch ( XMPPException e ) {
            m_logger.log( "Joining jabber failed", e );
        }
        try {
            while ( true ) {
                Message msg = m_in.take();
                try {
                    send( msg );
                } catch ( XMPPException e ) {
                    m_logger.log( "Could not send jabber message", e );
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
                    m_logger.log( String.format( "Could not deliver message to room %s: room not joined", rec ) );
                }
            }
        }
    }

    private void login() throws XMPPException {
        assert( m_connection == null );
        final int magic = new Random().nextInt( 1000 );
        final ConnectionConfiguration connconf = new ConnectionConfiguration( m_account.connectServer(), m_account
                .port() );
        connconf.setSecurityMode( m_account.sslRequired() ? SecurityMode.required : SecurityMode.enabled );
        connconf.setSendPresence( true );
        connconf.setReconnectionAllowed( true );

        try {
            m_connection = new XMPPConnection( connconf );
            m_connection.connect();
            m_connection.login( m_account.user(), m_account.password(), "Daytona" + magic );
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
    private Vector<String> m_admins;
    private String m_nick;
    private XMPPConnection m_connection;
    private Map<String, MultiUserChat> m_rooms;
    private Logger m_logger;

    public void chatCreated( Chat chat, boolean arg1 ) {
        // TODO check arg1
        chat.addMessageListener(  this );
    }


    private String helpText( String cmd ) {
        return "Available commands: :help, :list-rules, :add-rule, :delete-rule";
    }

    private boolean requiresAdminRights( String cmd ) {
        return !cmd.equals( "help" );
    }

    private static String commandName( String msg ) {
        String trimmed = msg.trim();
        if ( !trimmed.startsWith( ":" ) )
            return null;
        trimmed = trimmed.substring( 1 );
        String[] split = trimmed.split( " ", 2 );
        if ( split.length > 0 )
            return split[0];
        else
            return null;
    }


    public void processMessage( Chat chat, org.jivesoftware.smack.packet.Message msg ) {
        final String from = msg.getFrom();
        final String body = msg.getBody();

        final String cmd = commandName( body );

        try {
            if ( cmd == null ) {
                chat.sendMessage( helpText( "TODO" ) );
                return;
            }

            if ( requiresAdminRights( cmd ) && !m_admins.contains( from ) ) {
                chat.sendMessage( String.format( "You are not authorized to perform the command %s.", cmd ) );
                return;
            }
            if ( cmd.equals( "help" ) ) {
                chat.sendMessage( helpText( "TODO" ) );
                return;
            }
        } catch ( XMPPException e ) {
            m_logger.log( "Could not send jabber message", e );
        }
    }
}
