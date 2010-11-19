package com.kdab.restbot;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

public class Router implements Runnable {
	public Router( BlockingQueue<Message> inQ, BlockingQueue<Message> outQ, Vector<RoutingRule> rules ) {
		m_in = inQ;
		m_out = outQ;
		m_rules = rules;
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
		while ( true ) {
			try {
				for ( RoutingRule i : m_rules )
					i.applyTo( msg );
				m_out.put( msg );
				return;
			} catch ( InterruptedException e ) {
			}
		}
	}
	
	private BlockingQueue<Message> m_in;
	private BlockingQueue<Message> m_out;
	private Vector<RoutingRule> m_rules;
}
