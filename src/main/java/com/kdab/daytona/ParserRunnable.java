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

import java.util.concurrent.BlockingQueue;

public class ParserRunnable implements Runnable {
    public ParserRunnable( Parser parser, BlockingQueue<byte[]> in, BlockingQueue<Message> out, Logger logger ) {
        m_parser = parser;
        m_in = in;
        m_out = out;
        m_logger = logger;
    }

    public void run() {
        try {
            while ( true ) {
                byte[] raw = m_in.take();
                parseAndPut( raw );
            }
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
        }
    }

    private void parseAndPut( byte[] raw ) throws InterruptedException {
        Message msg = null;
        try {
            msg = m_parser.parse( raw );
        } catch ( Throwable e ) {
            m_logger.log( String.format( "Could not parse %s", m_parser.format() ), e );
            return;
        }

        m_out.put( msg );
    }

    private BlockingQueue<byte[]> m_in;
    private BlockingQueue<Message> m_out;
    private Logger m_logger;
    private Parser m_parser;
}
