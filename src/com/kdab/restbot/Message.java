package com.kdab.restbot;

import java.util.HashMap;

public class Message {
	
	public static Message createPoison() {
		Message msg = new Message();
		msg.setProperty( "com.kdab.control", "poison" );
		return msg;
	}
	
	public boolean isPoison() {
		return property( "com.kdab.control" ).equals( "poison" );
	}
	
	public final String text() {
		return m_text;
	}

	public final void setText( String text ) {
		m_text = text;
	}

	public final String property( String k ) {
		return m_properties.get( k );
	}
	public final void setProperty( String k, String v ) {
		m_properties.put( k, v );
	}
	
	private String m_text;
	private HashMap<String, String> m_properties;
}
