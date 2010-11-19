package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

public class Receiver implements Runnable {
	public Receiver( BlockingQueue<byte[]> outQ ) {
		m_out = outQ;
	}
	
	public void run() {
		String test1 = "<foo><text>Das Pferd frisst keinen Gurkensalat.</text><project>frankskram</project></foo>";
		String test2 = "<foo><text>I was routed here because project=make-o-matic.</text><project>make-o-matic</project></foo>";
		try {
			m_out.put( test1.getBytes() );
			m_out.put( test2.getBytes() );
		} catch ( InterruptedException e ) {
			
		}
	}
	
	private BlockingQueue<byte[]> m_out;
}
