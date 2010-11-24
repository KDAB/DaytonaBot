package com.kdab.restbot;

import java.io.*;
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
		Configuration config = new Configuration();
		BlockingQueue<byte[]> raw = new ArrayBlockingQueue<byte[]>( 1000 );
		BlockingQueue<Message> parsed = new ArrayBlockingQueue<Message>( 1000 );
		BlockingQueue<Message> routed = new ArrayBlockingQueue<Message>( 5000 );
		Parser p = new Parser( raw, parsed );
		Router r = new Router( parsed, routed, config.routingRules() );
		JabberBot b = new JabberBot( routed, config.account(), config.nick(), config.roomsToJoin() );
		new Thread( p ).start();
		new Thread( r ).start();
		new Thread( b ).start();

		m_out = raw;
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
    	
    	if ( format != "xml" ) {
    		//TODO handle unsupported format
    	}
    	
    	String error = "";
    	byte[] ba = IOUtils.toByteArray( request.getInputStream() );
    	try {
    		m_out.put( ba );
    	} catch ( InterruptedException e ) {
    		//TODO
    		error = e.toString();
    	}
    	
    	PrintWriter out = response.getWriter();
    	out.println( "Delivered." + error );
    	out.flush();
    	out.close();
    }
    
	private BlockingQueue<byte[]> m_out;
}