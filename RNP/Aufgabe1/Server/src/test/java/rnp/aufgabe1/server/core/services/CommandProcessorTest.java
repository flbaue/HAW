/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rnp.aufgabe1.server.core.*;
import rnp.aufgabe1.server.core.models.Client;
import rnp.aufgabe1.server.core.Command;
import rnp.aufgabe1.server.core.models.IncomingMessage;
import rnp.aufgabe1.server.core.models.Message;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.mockito.Mockito.mock;

public class CommandProcessorTest {

    private CommandProcessor commandProcessor;
    private BlockingDeque<IncomingMessage> receiverQueue;
    private BlockingDeque<Message> broadcasterQueue;
    private Set<Client> clients;
    private Server server = mock(Server.class);

    @Before
    public void setUp() throws Exception {
        receiverQueue = new LinkedBlockingDeque<>();
        broadcasterQueue = new LinkedBlockingDeque<>();
        clients = new HashSet<>();
        //when(server.shutdown()).then //TODO mock shutdown
        commandProcessor = new CommandProcessor(receiverQueue, broadcasterQueue, clients, "test", server);
    }

    @Test
    public void testLowercase() throws Exception {

        String text_a = "AbCdE";
        String text_b = commandProcessor.cmdLowercase(new Message(Command.LOWERCASE, text_a,
                mock(Client.class))).toString();
        Assert.assertEquals("OK abcde\n", text_b);
    }

    @Test
    public void testUppercase() throws Exception {

        String text_a = "AbCdE";
        String text_b = commandProcessor.cmdUppercase(new Message(Command.UPPERCASE, text_a,
                mock(Client.class))).toString();
        Assert.assertEquals("OK ABCDE\n", text_b);
    }

    @Test
    public void testReverse() throws Exception {

        String text_a = "AbCdE";
        String text_b = commandProcessor.cmdReverse(new Message(Command.REVERSE, text_a,
                mock(Client.class))).toString();
        Assert.assertEquals("OK EdCbA\n", text_b);
    }

    @Test
    public void testBye() throws Exception {

        String text_a = "BYE";
        String text_b = commandProcessor.cmdBye(new Message(Command.BYE, text_a, mock(Client.class))).toString();
        Assert.assertEquals("BYE\n", text_b);
    }

    @Test
    public void testShutdownWithRightPassword() throws Exception {

        String text_a = "test";
        String text_b = commandProcessor.cmdShutdown(new Message(Command.SHUTDOWN, text_a,
                mock(Client.class))).toString();
        Assert.assertEquals("OK_BYE\n", text_b);
    }

    @Test
    public void testShutdownWIthWrongPassword() throws Exception {

        String text_a = "abc";
        String text_b = commandProcessor.cmdShutdown(new Message(Command.SHUTDOWN, text_a,
                mock(Client.class))).toString();
        Assert.assertEquals("ERROR unauthorized request\n", text_b);
    }

    @Test
    public void testError() throws Exception {

        String text_a = "abc";
        String text_b = commandProcessor.cmdError(new Message(Command.ERROR, text_a, mock(Client.class))).toString();
        Assert.assertEquals("ERROR unknown command: ERROR\n", text_b);
    }

    @Test
    public void testParseIncomingMessageWithValidContent() throws Exception {
        IncomingMessage i1 = new IncomingMessage("FU bar\n", mock(Client.class));
        Message m1 = commandProcessor.parseIncomingMessage(i1);
        Assert.assertEquals(Command.ERROR, m1.getCommand());
        Assert.assertEquals("bar", m1.getText());
    }

    @Test
    public void testParseIncomingMessageWithInvalidContent() throws Exception {
        IncomingMessage i1 = new IncomingMessage("fubar", mock(Client.class));
        Message m1 = commandProcessor.parseIncomingMessage(i1);
        Assert.assertEquals(Command.ERROR, m1.getCommand());
        Assert.assertEquals("unknown command: fubar...", m1.getText());
    }
}