package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

public class JabberBot implements Runnable {
	public JabberBot( BlockingQueue<Message> in ) {
		m_in = in;
	}
		
	public void run() {
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
			send( msg );
		}
	}
	
	private void send( Message msg ) {
		
	}
	
	private void logout() {
		
	}

	private BlockingQueue<Message> m_in;
}
