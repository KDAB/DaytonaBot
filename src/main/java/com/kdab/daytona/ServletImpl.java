/*
    This file is part of Daytona.

    Copyright (c) 2010 Frank Osterfeld <frank.osterfeld@kdab.com>

    Daytona is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    Daytona is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.kdab.daytona;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.kdab.daytona.Configuration;
import com.kdab.daytona.JabberBot;
import com.kdab.daytona.JsonParser;
import com.kdab.daytona.Message;
import com.kdab.daytona.Router;
import com.kdab.daytona.XmlParser;

class Logger {
    public Logger( ServletConfig cfg ) {
        m_cfg = cfg;
    }

    public synchronized void close() {
        m_cfg = null;
    }

    public synchronized void log( String txt, Throwable t ) {
        if ( m_cfg != null )
            m_cfg.getServletContext().log( txt, t );
    }

    public synchronized void log( String txt ) {
        if ( m_cfg != null )
            m_cfg.getServletContext().log( txt );
    }

    private ServletConfig m_cfg;
}

public class ServletImpl extends HttpServlet {
    private static final long serialVersionUID = 8622072647253890417L;

    public ServletImpl() {
        m_queuesByFormat = new HashMap<String, BlockingQueue<byte[]>>();
        m_error = false;
    }

    @Override
    public void init( ServletConfig cfg ) {
        m_logger = new Logger( cfg );
        Configuration config = null;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( "/Users/frank/daytona-config.xml" ) ); //TODO where to put the file?
            config = new Configuration( props );
        } catch ( IOException e ) {
            System.err.println( e.getMessage() );
            cfg.getServletContext().log( "Could not load Daytona configuration file", e );
            m_error = true;
            return;
        } catch ( InvalidConfigurationException e ) {
            cfg.getServletContext().log( "Could not parse Daytona configuration file", e );
            m_error = true;
            return;
        }
        BlockingQueue<byte[]> rawXml = new ArrayBlockingQueue<byte[]>( 1000 );
        BlockingQueue<byte[]> rawJson = new ArrayBlockingQueue<byte[]>( 1000 );
        m_queuesByFormat.put( "xml", rawXml );
        m_queuesByFormat.put( "json", rawJson );
        BlockingQueue<Message> parsed = new ArrayBlockingQueue<Message>( 1000 );
        BlockingQueue<Message> routed = new ArrayBlockingQueue<Message>( 5000 );
        XmlParser xp = new XmlParser( rawXml, parsed, m_logger );
        JsonParser jp = new JsonParser( rawJson, parsed, m_logger );
        Router r = new Router( parsed, routed, config.routingRules() );
        JabberBot b = new JabberBot( routed, config.account(), config.nick(), config.admins(), config.roomsToJoin(), m_logger );
        m_workers = new Vector<Thread>();
        m_workers.add( new Thread( xp ) );
        m_workers.add( new Thread( jp ) );
        m_workers.add( new Thread( r ) );
        m_workers.add( new Thread( b ) );
        for ( Thread i : m_workers )
            i.start();
    }

    @Override
    public void destroy() {
        m_logger.close();
        for ( Thread i : m_workers )
            i.interrupt();
    }

    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println( "Use PUT to deliver content to notify" );
        out.flush();
        out.close();
    }

    @Override
    public void doPut( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        if ( m_error ) {
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            PrintWriter out = response.getWriter();
            out.println( "Daytona not operational. See server log for details." );
            out.flush();
            out.close();
            return;
        }

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

    private Map<String, BlockingQueue<byte[]>> m_queuesByFormat;
    private Vector<Thread> m_workers;
    private boolean m_error;
    private Logger m_logger;
}
