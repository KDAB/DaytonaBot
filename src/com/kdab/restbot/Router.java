package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

public class Router implements Runnable {
	public Router( BlockingQueue<Message> inQ, BlockingQueue<Message> outQ ) {
		m_in = inQ;
		m_out = outQ;
	}

	public void run() {
		while ( true ) {
			try {
				final Message m = m_in.take();
				route( m );						
				if ( m.isPoison() )
					return;
			} catch ( InterruptedException e ) {
				//TODO correct?
			}
		}
	}

	public void route( Message msg ) {
		//TODO assign sender
		while ( true ) {
			try {
				m_out.put( msg );
				return;
			} catch ( InterruptedException e ) {
			}
		}
	}
	
	BlockingQueue<Message> m_in;
	BlockingQueue<Message> m_out;
}
