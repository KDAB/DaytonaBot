package com.kdab.daytona;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class JsonParserTest {

    @Test
    public void valid() throws ParserException {
        JsonParser parser = new JsonParser();
        Message msg = parser.parse( "{ \"text\" : \"foobar\", \"project\" : \"daytona\" }".getBytes() );
        Message expected = new Message();
        expected.setProperty( "text", "foobar" );
        expected.setProperty( "project", "daytona" );
        assertEquals(  msg, expected );
    }

    @Test(expected=ParserException.class)
    public void invalid() throws ParserException {
        JsonParser parser = new JsonParser();
        parser.parse( "{ \"text\" : \"foobar\"".getBytes() );
    }
}
