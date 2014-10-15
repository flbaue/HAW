/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server;

import org.junit.Test;
import rnp.aufgabe1.server.Command;
import rnp.aufgabe1.server.Message;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    @Test
    public void testGetCommand() throws Exception {
        Message message = new Message(Command.REVERSE, "abc");
        assertEquals(Command.REVERSE, message.getCommand());
    }

    @Test
    public void testGetText() throws Exception {
        Message message = new Message(Command.REVERSE, "abc");
        assertEquals("abc", message.getText());

        Message message2 = new Message(Command.REVERSE, "");
        assertEquals("", message2.getText());

        Message message3 = new Message(Command.REVERSE);
        assertEquals("", message3.getText());

        Message message4 = new Message(Command.REVERSE, null);
        assertEquals("", message4.getText());
    }

    @Test
    public void testToString() throws Exception {
        Message message = new Message(Command.REVERSE, "abc");
        assertEquals("REVERSE abc\n", message.toString());

        Message message2 = new Message(Command.BYE);
        assertEquals("BYE\n", message2.toString());
    }
}