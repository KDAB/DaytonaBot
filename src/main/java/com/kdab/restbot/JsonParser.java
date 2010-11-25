package com.kdab.restbot;

import java.util.concurrent.BlockingQueue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JsonParser implements Runnable {
    public JsonParser( BlockingQueue<byte[]> in, BlockingQueue<Message> out ) {
        m_in = in;
        m_out = out;
    }

    public void run() {
        try {
            while ( true ) {
                byte[] raw = m_in.take();
                if ( !parseAndPut( raw ) ) // poison -> shutdown
                    return;
            }
        } catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean parseAndPut( byte[] raw ) throws InterruptedException {
        Message msg = null;
        try {
            msg = parse( raw );
        } catch ( JSONException e ) {
            System.err.println( e );
            return true;
        }

        final boolean isPoison = msg.isPoison();
        m_out.put( msg );
        return !isPoison;
    }

    private Message parse( byte[] raw ) throws JSONException {
        if ( raw == null )
            return Message.createPoison();
        final Message msg = new Message();
        JSONObject map = new JSONObject( new String( raw ) );
        for ( String i : JSONObject.getNames( map ) )
            msg.setProperty( i,map.getString( i ) );
        return msg;
    }

    private BlockingQueue<byte[]> m_in;
    private BlockingQueue<Message> m_out;
}
