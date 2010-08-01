package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

public class Parser implements Runnable {
	public Parser( BlockingQueue<byte[]> in, BlockingQueue<Message> out ) {
		m_in = in;
		m_out = out;
	}
	
	public void run() {
		while ( true ) {
			byte[] raw = null;
			try {
				raw = m_in.take();
			} catch ( InterruptedException e ) {
				//TODO: correct?
				continue;
			}
			if ( raw == null ) { // supposed to quit, send poison to next in queue and quit:
				Message poison = Message.createPoison();
				while ( true ) {
					try {
						m_out.put( poison );
						return; // we are done
					} catch ( InterruptedException e ) {
					}
				}
			}
			
			//TODO parse
		}
	}
	
	private BlockingQueue<byte[]> m_in;
	private BlockingQueue<Message> m_out;
}
