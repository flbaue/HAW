/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core.services;

import rnp.aufgabe1.server.core.Command;
import rnp.aufgabe1.server.core.Server;
import rnp.aufgabe1.server.core.models.Client;
import rnp.aufgabe1.server.core.models.IncomingMessage;
import rnp.aufgabe1.server.core.models.Message;

import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rnp.aufgabe1.server.core.Command.*;

/**
 * The CommandProcessor is running in it's own thread. It waits for an IncomingMessage object on the receiverQueue. Once
 * an IncomingMessage object is received it will be validated and transformed into an ordinary Message object, which
 * then is processed. The processing result will be put into a new Message object and then added to the
 * broadcasterQueue.
 * <p>
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class CommandProcessor implements Runnable {

    public static final String UNKNOWN_COMMAND = "unknown command: ";
    public static final String UNAUTHORIZED_REQUEST = "unauthorized request";

    // Pattern to validate the IncomingMessage content
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


    /**
     * The Thread runs until it is interrupted. It always waits until it can take the first IncomingMessage from teh
     * receiverQueue. The IncomingMessage is then validated and transformed to an Message object and processed. The
     * result will be added as Message object to the broadcasterQueue. The thread interrupts it's self when shutdown is
     * true and no registered clients are left.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait for incoming message
                final IncomingMessage incomingMessage = receiverQueue.takeFirst();
                final Message messageIn = parseIncomingMessage(incomingMessage);
                final Message messageOut;

                // only clients that we know the port of can be considered
                if (messageIn.getCommand() != HELLO && !clients.contains(messageIn.getClient())) {
                    messageOut = unregisteredClient(messageIn);
                } else {

                    // process message commands
                    switch (messageIn.getCommand()) {
                        case HELLO:
                            messageOut = cmdHello(messageIn);
                            break;
                        case LOWERCASE:
                            messageOut = cmdLowercase(messageIn);
                            break;
                        case UPPERCASE:
                            messageOut = cmdUppercase(messageIn);
                            break;
                        case REVERSE:
                            messageOut = cmdReverse(messageIn);
                            break;
                        case BYE:
                            messageOut = cmdBye(messageIn);
                            break;
                        case SHUTDOWN:
                            messageOut = cmdShutdown(messageIn);
                            break;
                        default:
                            messageOut = cmdError(messageIn);
                            break;
                    }
                }

                // IGNORE messages mean that we don't know the client's port, probably because the client did not
                // register right.
                if (messageOut.getCommand() != IGNORE) {
                    broadcasterQueue.add(messageOut);
                }

                // if the server is shutting down and no clients are left, we interrupt this thread to stop it.
                if (shutdown && receiverQueue.isEmpty() && !server.isReceiverThreadAlive()) {
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException e) {
                // interrupts have to be repeated if caught
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Server: CommandProcessorThread is off");
    }

    /**
     * If a client sends a valid Message but is not registered first, the request must be ignored because the clients
     * port to receive messages is unknown.
     *
     * @param message the client's message.
     * @return an IGNORE Message.
     */
    private Message unregisteredClient(Message message) {
        return new Message(IGNORE, message.getText(), message.getClient());
    }


    /**
     * The IncomingMessage is validated with a regular expression and then parsed. The content String is split and a
     * Commands object, String text object and the clients information are stored into a new Message object. In the case
     * of an error (e.g. validation error, parsing error, etc.) an "ERROR" message will be generated.
     *
     * @param incomingMessage of the client. Containing the content string and the client's information.
     * @return A Message object for further processing.
     */
    Message parseIncomingMessage(IncomingMessage incomingMessage) {
        String content = incomingMessage.getContent();
        Matcher matcher = messagePattern.matcher(content);

        if (matcher.matches()) {
            int space = content.indexOf("\u0020");
            String cmdName = content.substring(0, space);
            String text = content.substring(space).trim();
            Command command;
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

    /**
     * The "Hello" message of the client is here processed. The Client has to sent this message first, to register with
     * this server. The message has to have the format "HELLO [port]" whereby [port] is the port number on which the
     * client can receive messages from the server. The Client is then put into the list of known clients.
     *
     * @param message of the client that contains the client's port to receive messages.
     * @return a new Message object that response with "HELLO" to the client. Or an IGNORE message, if the clients port
     * is not valid. It can not be an ERROR response, because there is no port to send it to ;-).
     */
    private Message cmdHello(Message message) {
        try {
            int port = Integer.parseInt(message.getText());
            message.getClient().setPortIn(port);
        } catch (NumberFormatException e) {
            return new Message(IGNORE, message.getText(), message.getClient());
        }
        clients.add(message.getClient());
        return new Message(HELLO, "", message.getClient());
    }

    /**
     * Creates an ERROR response Message. If the Message already is an ERROR Message, it is not changed.
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdError(Message message) {
        if (message.getCommand() == ERROR) {
            return message; // e.g. parsing errors
        } else {
            return new Message(ERROR, UNKNOWN_COMMAND + message.getCommand(), message.getClient());
        }
    }

    /**
     * Starts the shutdown process of the server, if the Message contains the right password. The client's session is
     * then also removed and a OK_BYE response is generated. If the password is wrong, an ERROR Message is generated.
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdShutdown(Message message) {
        if (message.getText().equals(secretToken)) {
            clients.remove(message.getClient());
            server.prepareShutdown();
            return new Message(OK_BYE, "", message.getClient());
        } else {
            return new Message(ERROR, UNAUTHORIZED_REQUEST, message.getClient());
        }
    }

    /**
     * Removes the client's session and generates a BYE Message.
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdBye(Message message) {
        clients.remove(message.getClient());
        return new Message(BYE, "", message.getClient());
    }

    /**
     * Text manipulation
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdReverse(Message message) {
        String text = message.getText();
        Client client = message.getClient();

        StringBuilder stringBuilder = new StringBuilder(255);
        for (int i = text.length() - 1; i >= 0; i--) {
            stringBuilder.append(text.charAt(i));
        }

        return new Message(OK, stringBuilder.toString(), client);
    }

    /**
     * Text manipulation
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdLowercase(Message message) {
        String text = message.getText().trim();
        Client client = message.getClient();

        text = text.toLowerCase();

        return new Message(OK, text, client);
    }

    /**
     * Text manipulation
     *
     * @param message the parsed incoming Message.
     * @return the response Message for the client.
     */
    Message cmdUppercase(Message message) {
        String text = message.getText().trim();
        Client client = message.getClient();

        text = text.toUpperCase();

        return new Message(OK, text, client);
    }

    /**
     * Sets the CommandProcessor in the shutdown mode.
     */
    public void shutdown() {
        shutdown = true;
    }
}
