package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
			while ( true ) {
				try {
					final Message msg = parse( raw );
					m_out.put( msg );
				} catch ( InterruptedException e ) {
				} catch ( Exception e ) {				
				}
			}		
		}
	}
	
	private Message parse( byte[] raw ) throws Exception {
		if ( raw == null ) 
			return Message.createPoison();
		final Message msg = new Message();
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document doc = builder.parse( new ByteArrayInputStream( raw ) );
		final Element root = doc.getDocumentElement();
		final NodeList children = root.getChildNodes();
		return msg;
	}

	private static String restBotNS = "http://www.kdab.com/restbot";
	
	private static String getFirstOrThrow( Element root, String name ) throws Exception {
		final NodeList list = root.getElementsByTagName( name );
		if ( list.getLength() == 0 )
			throw new Exception( "Event object has no \"" + name + "\" element." );
		final String str = list.item( 0 ).getTextContent();
		return str != null ? str : "";
	}
	
	private static String shorten( String msg ) {
		//TODO
		return msg;
	}
	
	
	public static Message parseMessage( InputStream stream ) throws Exception {
		try {
			final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document doc = builder.parse( stream );
			final Element root = doc.getDocumentElement();
			final String committer = getFirstOrThrow( root, "committer" );
			final String message = getFirstOrThrow( root, "message" );
			final String path = getFirstOrThrow( root, "path" );
			final String revision = getFirstOrThrow( root, "revision" );
			return createMessage(committer, revision, path, message);
		} catch ( final Exception e ) {
			throw e;		}
	}

	private BlockingQueue<byte[]> m_in;
	private BlockingQueue<Message> m_out;
}
