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

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlParser implements Parser {
    public Message parse( byte[] raw ) throws ParserException {
        final Message msg = new Message();
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {
            throw new ParserException( e );
        }
        Document doc = null;
        try {
            doc = builder.parse( new ByteArrayInputStream( raw ) );
        } catch ( Throwable t ) {
            throw new ParserException( t );
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

    public String format() {
        return "XML";
    }
}
