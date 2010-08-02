package com.kdab.restbot;

import java.util.HashMap;

public class Message {
	
	public static Message createPoison() {
		Message msg = new Message();
		msg.setProperty( "com.kdab.restbot.control", "poison" );
		return msg;
	}
	
	public boolean isPoison() {
		return property( "com.kdab.restbot.control" ).equals( "poison" );
	}
	
	public final String receiver() {
		return m_receiver;
	}
	
	public final void setReceiver( String receiver ) {
		m_receiver = receiver;
	}
	
	public final String text() {
		return property( "text" );
	}

	public final String property( String k ) {
		return m_properties.get( k );
	}
	public final void setProperty( String k, String v ) {
		m_properties.put( k, v );
	}
	
	private String m_receiver;
	private HashMap<String, String> m_properties;
}
