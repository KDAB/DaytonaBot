/*
    This file is part of PutBot.

    Copyright (c) 2010 Frank Osterfeld <frank.osterfeld@kdab.com>

    PutBot is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    Kleopatra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.kdab.restbot;

import java.util.Vector;
import java.util.concurrent.BlockingQueue;

public class Router implements Runnable {
    public Router( BlockingQueue<Message> inQ, BlockingQueue<Message> outQ, Vector<RoutingRule> rules ) {
        m_in = inQ;
        m_out = outQ;
        m_rules = rules;
    }

    public void run() {
        try {
            while ( true ) {
                final Message m = m_in.take();
                route( m );
            }
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
        }
    }

    public void route( Message msg ) throws InterruptedException {
        for ( RoutingRule i : m_rules )
            i.applyTo( msg );
        m_out.put( msg );
    }

    private BlockingQueue<Message> m_in;
    private BlockingQueue<Message> m_out;
    private Vector<RoutingRule> m_rules;
}
