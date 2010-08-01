package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Receiver implements HttpHandler implements Runnable {
	public Receiver( BlockingQueue<byte[]> outQ ) {
		m_out = outQ;
	}
	
	public void run() {
		HttpServer server = new HttpServer;
	}
	
	private BlockingQueue<byte[]> m_out;
}
