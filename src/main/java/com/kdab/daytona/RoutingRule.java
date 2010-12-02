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

import java.util.Vector;

interface BinaryPredicate {
    public boolean isTrue( String lhs, String rhs );

    public String name();
}

class Equals implements BinaryPredicate {
    public boolean isTrue( String lhs, String rhs ) {
        if ( lhs == null )
            return rhs == null;
        else
            return lhs.equals( rhs );
    }

    public String name() {
        return "equals";
    }
}

class Contains implements BinaryPredicate {
    public boolean isTrue( String lhs, String rhs ) {
        if ( rhs == null || rhs.isEmpty() )
            return true;
        else
            return lhs.contains( rhs );
    }

    public String name() {
        return "contains";
    }
}

class Condition {
    public Condition( String property, BinaryPredicate pred, String value ) {
        m_property = property;
        m_pred = pred;
        m_value = value;
    }

    public boolean satisfiedBy( Message m ) {
        String p = m.property( m_property );
        return m_pred.isTrue( p, m_value );
    }

    @Override
    public String toString() {
        return String.format( "%s :%s \"%s\"", m_property, m_pred.name(), m_value );
    }

    private String m_property;
    private String m_value;
    private BinaryPredicate m_pred;
}

public class RoutingRule {
    public RoutingRule( Vector<Condition> conditions, Message.ReceiverType receiverType, String receiver ) {
        m_conditions = conditions;
        m_receiverType = receiverType;
        m_receiver = receiver;
    }

    public RoutingRule( String str ) {

        //TODO
        // Receiver := JID
        // ReceiverType := "Room" | "User"
        // Predicate =  ":contains"|":equals'"
        // Property = A-Za-z0-9._-
        // Condition := Property Predicate \"String\"
        // Rule := Condition [, Condition]* => ReceiverType Receiver
    }

    public String receiver() {
        return m_receiver;
    }

    public Message.ReceiverType receiverType() {
        return m_receiverType;
    }

    public void applyTo( Message m ) {
        if ( matches( m ) )
            m.addReceiver( m_receiver, m_receiverType );
    }

    public boolean matches( Message m ) {
        for ( Condition i : m_conditions )
            if ( !i.satisfiedBy( m ) )
                return false;
        return true;
    }

    @Override
    public String toString() {
        String typestr = m_receiverType == Message.ReceiverType.Room ? "room" : "user";

        String conds = "";
        for ( Condition i : m_conditions )
            if ( !conds.isEmpty() )
                conds += ", " + i.toString();
            else
                conds += i.toString();
        return String.format( "%s => %s %s", conds, typestr, m_receiver );
    }

    private Vector<Condition> m_conditions;
    private String m_receiver;
    private Message.ReceiverType m_receiverType;
}
