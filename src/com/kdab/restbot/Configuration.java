package com.kdab.restbot;

import java.util.Vector;

public class Configuration {
	public Configuration() {
		m_nick = "RESTBot";
		m_roomsToJoin = new Vector<String>();
		m_roomsToJoin.add( "royalblue@conference.kdab.com" );
		m_routingRules = new Vector<RoutingRule>();
		Vector<Condition> cond1 = new Vector<Condition>();
		cond1.add( new Condition( "project", new Equals(), "frankskram" ) );
		RoutingRule rule1 = new RoutingRule( cond1, Message.ReceiverType.User, "frank@kdab.com" );
		Vector<Condition> cond2 = new Vector<Condition>();
		cond2.add( new Condition( "project", new Equals(), "make-o-matic" ) );
		RoutingRule rule2 = new RoutingRule( cond2, Message.ReceiverType.Room, "royalblue@conference.kdab.com" );
		m_routingRules.add( rule1 );
		m_routingRules.add( rule2 );
	}

	public final Vector<String> roomsToJoin() {
		return m_roomsToJoin;
	}
	
	public final String nick() {
		return m_nick;
	}
	
	public final Vector<RoutingRule> routingRules() {
		return m_routingRules;
	}
	
	private Vector<String> m_roomsToJoin;
	private Vector<RoutingRule> m_routingRules;
	
	private String m_nick;
}
