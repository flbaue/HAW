/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rnp.aufgabe1.server.core.Commands.*;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class CommandProcessor implements Runnable {

    public static final String UNKNOWN_COMMAND = "unknown command: ";
    public static final String UNAUTHORIZED_REQUEST = "unauthorized request";

    private static final Pattern messagePattern = Pattern.compile("[A-Z]+(\u0020.*)?\n");

    private final BlockingDeque<IncomingMessage> receiverQueue;
    private final BlockingDeque<Message> broadcasterQueue;
    private final Set<Client> clients;
    private final String secretToken;
    private final Server server;
    private boolean shutdown = false;

    public CommandProcessor(final BlockingDeque<IncomingMessage> receiverQueue, final BlockingDeque<Message> broadcasterQueue,
                            final Set<Client> clients, final String secretToken, final Server server) {
        this.receiverQueue = receiverQueue;
        this.broadcasterQueue = broadcasterQueue;
        this.clients = clients;
        this.secretToken = secretToken;
        this.server = server;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                IncomingMessage incomingMessage = receiverQueue.takeFirst();
                Message message = parseIncomingMessage(incomingMessage);

                switch (message.getCommand()) {
                    case LOWERCASE:
                        message = cmdLowercase(message);
                        break;
                    case UPPERCASE:
                        message = cmdUppercase(message);
                        break;
                    case REVERSE:
                        message = cmdReverse(message);
                        break;
                    case BYE:
                        message = cmdBye(message);
                        break;
                    case SHUTDOWN:
                        message = cmdShutdown(message);
                        break;
                    default:
                        message = cmdError(message);
                        break;
                }
                broadcasterQueue.add(message);

                if(shutdown && clients.isEmpty()){
                    Thread.currentThread().interrupt();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //TODO evtl andere prozesse killen? speziel receiver
    }

    Message parseIncomingMessage(IncomingMessage incomingMessage) {
        String content = incomingMessage.getContent();
        Matcher matcher = messagePattern.matcher(content);

        if (matcher.matches()) {
            int space = content.indexOf("\u0020");
            String cmdName = content.substring(0, space);
            String text = content.substring(space).trim();
            Commands command;
            try {
                command = valueOf(cmdName);
            } catch (IllegalArgumentException e) {
                command = ERROR;
            }
            return new Message(command, text, incomingMessage.getClient());

        } else {
            int end = (content.length() < 200) ? content.length() : 230;
            return new Message(ERROR, UNKNOWN_COMMAND + incomingMessage.getContent().substring(0, end) + "...",
                    incomingMessage.getClient());
        }
    }

    Message cmdError(Message message) {
        //TODO Fälle beachten, evtl. splitten für bessere Fehlermeldung
        return new Message(ERROR, UNKNOWN_COMMAND + message.getCommand(), message.getClient());
    }

    Message cmdShutdown(Message message) {
        if (message.getText().equals(secretToken)) {
            clients.remove(message.getClient());
            server.shutdown();
            return new Message(OK_BYE, "", message.getClient());
        } else {
            return new Message(ERROR, UNAUTHORIZED_REQUEST, message.getClient());
        }
    }

    Message cmdBye(Message message) {
        clients.remove(message.getClient());
        return new Message(BYE, "", message.getClient());
    }

    Message cmdReverse(Message message) {
        String text = message.getText();
        Client client = message.getClient();

        StringBuilder stringBuilder = new StringBuilder(255);
        for (int i = text.length() - 1; i >= 0; i--) {
            stringBuilder.append(text.charAt(i));
        }

        return new Message(OK, stringBuilder.toString(), client);
    }

    Message cmdLowercase(Message message) {
        String text = message.getText().trim();
        Client client = message.getClient();

        text = text.toLowerCase();

        return new Message(OK, text, client);
    }

    Message cmdUppercase(Message message) {
        String text = message.getText().trim();
        Client client = message.getClient();

        text = text.toUpperCase();

        return new Message(OK, text, client);
    }

    public void shutdown() {
        shutdown = true;
    }
}
