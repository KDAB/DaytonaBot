package com.kdab.restbot;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;

public class JabberBot implements Runnable {
	public JabberBot( BlockingQueue<Message> in, Account account ) {
		m_in = in;
		m_account = account;
	}
		
	public void run() {
		try {
			login();
		} catch ( XMPPException e ) {
			System.err.println( e );
			//TODO how to report?
		}
		while ( true ) {
			Message msg;
			try {
				msg = m_in.take();
			} catch ( InterruptedException e ) {
				//TODO correct?
				continue;
			}
			if ( msg.isPoison() ) {
				logout();
				return;
			}
			try {
				send( msg );
			} catch ( XMPPException e ) {
				System.err.println( e );
				//TODO how to report?
			}
		}
	}
	
	private void send( Message msg ) throws XMPPException {
		final String rec = msg.receiver();
		assert( rec != null );
		assert( !rec.isEmpty() );
		Chat chat = m_connection.getChatManager().createChat( rec, null );
		chat.sendMessage( msg.text() );
	}
	
	private void login() throws XMPPException {
		assert( m_connection == null );
        final int magic = new Random().nextInt( 1000 );
        final ConnectionConfiguration connconf = new ConnectionConfiguration( m_account.getServer(), m_account.getPort() );
        connconf.setSecurityMode( SecurityMode.required );
        try
        {
            m_connection = new XMPPConnection( connconf );
            m_connection.connect();
            m_connection.login( m_account.getUser(), m_account.getPassword(), "RestBot" + magic );
        }
        catch ( XMPPException e )
        {
            //TODO: check if reset to null is enough for cleanup
            m_connection = null;
            throw e;
        }	
	}
	
	private void logout() {
		
	}

	private BlockingQueue<Message> m_in;
	private Account m_account;
    XMPPConnection m_connection;

}
