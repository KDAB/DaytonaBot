package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser implements Runnable {
    public Parser( BlockingQueue<byte[]> in, BlockingQueue<Message> out ) {
        m_in = in;
        m_out = out;
    }

    public void run() {
        try {
            while ( true ) {
                byte[] raw = m_in.take();
                if ( !parseAndPut( raw ) ) // poison -> shutdown
                    return;
            }
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean parseAndPut( byte[] raw ) throws InterruptedException {
        Message msg = null;
        try {
            msg = parse( raw );
        } catch ( SAXException e ) {
            System.err.println( e );
            return true;
        }

        final boolean isPoison = msg.isPoison();
        m_out.put( msg );
        return !isPoison;
    }

    private Message parse( byte[] raw ) throws SAXException {
        if ( raw == null )
            return Message.createPoison();
        final Message msg = new Message();
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {
            System.err.println( "Could not create document builder: " + e );
            // TODO report fatal error
        }
        Document doc = null;
        try {
            doc = builder.parse( new ByteArrayInputStream( raw ) );
        } catch ( IOException e ) {
            System.err.println( "Impossible IOException while reading from a byte array: " + e );
            // TODO report fatal error
        }
        final Element root = doc.getDocumentElement();
        final NodeList children = root.getChildNodes();
        for ( int i = 0; i < children.getLength(); ++i ) {
            final Node n = children.item( i );
            if ( n.getNodeType() != Node.ELEMENT_NODE )
                continue;
            final Element e = (Element) n;
            final String key = e.getTagName();
            final String value = e.getTextContent();
            msg.setProperty( key, value );
        }
        return msg;
    }

    private BlockingQueue<byte[]> m_in;
    private BlockingQueue<Message> m_out;
}
