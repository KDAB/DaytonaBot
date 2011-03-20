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

import java.util.HashMap;
import java.util.Vector;

public class Message {
    enum ReceiverType {
        User, Room
    }

    class Receiver {
        public Receiver( String r, ReceiverType t ) {
            receiver = r;
            type = t;
        }

        @Override
        public boolean equals( Object oother ) {
            if ( this == oother )
                return true;
            if ( !(oother instanceof Receiver ) )
                return false;
            Receiver other = (Receiver)oother;
            return Utils.areEqual( receiver, other.receiver )
                && Utils.areEqual( type, other.type );
        }

        public final String receiver;
        public final ReceiverType type;
    }

    public Message() {
        m_properties = new HashMap<String, String>();
        m_receivers = new Vector<Receiver>();
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

    @Override
    public boolean equals( Object oother ) {
        if ( this == oother )
            return true;
        if ( !(oother instanceof Message ) )
            return false;
        Message other = (Message)oother;
        return Utils.areEqual( m_receivers, other.m_receivers )
            && Utils.areEqual( m_properties, other.m_properties );
    }

    private Vector<Receiver> m_receivers;
    private HashMap<String, String> m_properties;
}
