package com.kdab.restbot;

import java.util.Vector;

interface BinaryPredicate {
	public boolean isTrue( String lhs, String rhs );
}

class Equals implements BinaryPredicate {
	public boolean isTrue( String lhs, String rhs ) {
		if ( lhs == null )
			return rhs == null;
		else
			return lhs.equals( rhs );
	}
}

class Contains implements BinaryPredicate {
	public boolean isTrue( String lhs, String rhs ) {
		if ( rhs == null || rhs.isEmpty() )
			return true;
		else
			return lhs.contains( rhs );
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
	
	private Vector<Condition> m_conditions;
	private String m_receiver;
	private Message.ReceiverType m_receiverType;
}
