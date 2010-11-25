package com.kdab.restbot;

import java.util.HashMap;
import java.util.Vector;

public class Message {

    public static Message createPoison() {
        Message msg = new Message();
        msg.setProperty( "com.kdab.restbot.control", "poison" );
        return msg;
    }

    enum ReceiverType {
        User, Room
    }

    class Receiver {
        public Receiver( String r, ReceiverType t ) {
            receiver = r;
            type = t;
        }

        public final String receiver;
        public final ReceiverType type;
    }

    public Message() {
        m_properties = new HashMap<String, String>();
        m_receivers = new Vector<Receiver>();
    }

    public boolean isPoison() {
        final String v = property( "com_kdab_restbot_control" );
        return v != null && v.equals( "poison" );
    }

    public final Vector<Receiver> receivers() {
        return m_receivers;
    }

    public final void addReceiver( String receiver, ReceiverType type ) {
        m_receivers.add( new Receiver( receiver, type ) );
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

    private Vector<Receiver> m_receivers;
    private HashMap<String, String> m_properties;
}
