package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

public class Receiver implements Runnable {
	public Receiver( BlockingQueue<byte[]> outQ ) {
		m_out = outQ;
	}
	
	public void run() {
		String test = "<foo><text>hello</text></foo>";
		try {
			m_out.put( test.getBytes() );
		} catch ( InterruptedException e ) {
			
		}
	}
	
	private BlockingQueue<byte[]> m_out;
}
