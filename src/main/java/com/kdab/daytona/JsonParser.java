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
import org.json.JSONException;
import org.json.JSONObject;


public class JsonParser implements Runnable {
    public JsonParser( BlockingQueue<byte[]> in, BlockingQueue<Message> out ) {
        m_in = in;
        m_out = out;
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
            msg = parse( raw );
        } catch ( JSONException e ) {
            System.err.println( e ); //TODO log error? report somewhere?
            return;
        }

        m_out.put( msg );
    }

    private Message parse( byte[] raw ) throws JSONException {
        assert( raw != null );
        final Message msg = new Message();
        JSONObject map = new JSONObject( new String( raw ) );
        for ( String i : JSONObject.getNames( map ) )
            msg.setProperty( i,map.getString( i ) );
        return msg;
    }

    private BlockingQueue<byte[]> m_in;
    private BlockingQueue<Message> m_out;
}
