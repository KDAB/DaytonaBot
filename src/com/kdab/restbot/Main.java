package com.kdab.restbot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
	public static void main( String[] args ) {
		BlockingQueue<byte[]> raw = new ArrayBlockingQueue<byte[]>( 1000 );
		BlockingQueue<Message> parsed = new ArrayBlockingQueue<Message>( 1000 );
		BlockingQueue<Message> routed = new ArrayBlockingQueue<Message>( 5000 );
		Receiver rec = new Receiver( raw );
		//TODO setup: port 
		Parser p = new Parser( raw, parsed );
		Router r = new Router( parsed, routed );
		//TODO setup
		JabberBot b = new JabberBot( routed );
		//TODO setup
		new Thread( rec ).start();
		new Thread( p ).start();
		new Thread( r ).start();
		new Thread( b ).start();
	}
}
