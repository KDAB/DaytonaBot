package com.kdab.restbot;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.*;                                                         
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;

import com.kdab.restbot.Configuration;
import com.kdab.restbot.JabberBot;
import com.kdab.restbot.Message;
import com.kdab.restbot.Parser;
import com.kdab.restbot.Router;

public class ServletImpl extends HttpServlet {
	public ServletImpl() {
		m_queuesByFormat = new HashMap<String, BlockingQueue<byte[]>>();
	}
	
	public void init( ServletConfig cfg ) {
		Configuration config = new Configuration();
		BlockingQueue<byte[]> rawXml = new ArrayBlockingQueue<byte[]>( 1000 );
		m_queuesByFormat.put( "xml", rawXml );
		BlockingQueue<Message> parsed = new ArrayBlockingQueue<Message>( 1000 );
		BlockingQueue<Message> routed = new ArrayBlockingQueue<Message>( 5000 );
		Parser p = new Parser( rawXml, parsed );
		Router r = new Router( parsed, routed, config.routingRules() );
		JabberBot b = new JabberBot( routed, config.account(), config.nick(), config.roomsToJoin() );
		m_workers = new Vector<Thread>();
		m_workers.add( new Thread( p ) );
		m_workers.add( new Thread( r ) );
		m_workers.add( new Thread( b ) );
		for ( Thread i : m_workers )
			i.start();
	}
	
	public void destroy() {
		for ( Thread i : m_workers )
			i.interrupt();
	}
	
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException { 
        PrintWriter out = response.getWriter();
        out.println( "Use PUT to deliver content to notify" );
        out.flush();
        out.close();
    }

    public void doPut(HttpServletRequest request,
            HttpServletResponse response)
    	throws ServletException, IOException {
    	String format = request.getParameter( "format" );
    	if ( format == null )
    		format = "xml";
    	
    	if ( !m_queuesByFormat.containsKey( format ) ) {
    	   	response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
    		PrintWriter out = response.getWriter();
        	out.println( String.format( "Unknown format \'%s\'.", format ) );
        	out.flush();
        	out.close();
        	return;
    	}
    	
    	byte[] ba = IOUtils.toByteArray( request.getInputStream() );
    	try {
    		m_queuesByFormat.get( format ).put( ba );
    	} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
    	}
    	
    	PrintWriter out = response.getWriter();
    	out.println( "Message received." );
    	out.flush();
    	out.close();
    }
    
	private Map<String,BlockingQueue<byte[]>> m_queuesByFormat;
	private Vector<Thread> m_workers;
}