/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.newserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rnp.aufgabe1.server.newserver.Command.*;

/**
 * Created by flbaue on 13.10.14.
 */
public class CommandProcessor {

    // Pattern to validate the IncomingMessage content
    private static final Pattern messagePattern = Pattern.compile("[A-Z]+(\u0020.*)?\n");
    private static final String UNKNOWN_COMMAND = "unknown command: ";
    private final ConnectionWorker connectionWorker;

    public CommandProcessor(ConnectionWorker connectionWorker) {
        this.connectionWorker = connectionWorker;
    }

    public String process(String input) {
        Message messageIn = parseMessage(input);
        Message messageOut;
        switch (messageIn.getCommand()) {
            case LOWERCASE:
                messageOut = cmdLowercase(messageIn.getText());
                break;
            case UPPERCASE:
                messageOut = cmdUppercase(messageIn.getText());
                break;
            case REVERSE:
                messageOut = cmdReverse(messageIn.getText());
                break;
            case BYE:
                messageOut = cmdBye();
                break;
            case SHUTDOWN:
                messageOut = cmdShutdown(messageIn.getText());
                break;
            default:
                messageOut = cmdError(messageIn.getText());
                break;
        }
        return messageOut.toString();
    }

    private Message parseMessage(final String input) {
        Matcher matcher = messagePattern.matcher(input);
        if (matcher.matches()) {
            int space = input.indexOf("\u0020");
            String cmdName;
            if(space >= 0) {
                cmdName = input.substring(0, space);
            } else {
                cmdName = input;
            }
            Command command;
            String text;
            try {
                command = valueOf(cmdName);
                text = input.substring(space).trim();
            } catch (IllegalArgumentException e) {
                command = ERROR;
                int end = (input.length() < 200) ? input.length() : 230;
                text = input.substring(0, end) + "...";
            }
            return new Message(command, text);
        } else {
            int end = (input.length() < 200) ? input.length() : 230;
            return new Message(ERROR, UNKNOWN_COMMAND + input.substring(0, end) + "...");
        }
    }

    private Message cmdError(String input) {
        return new Message(ERROR, "Unknown command");
    }

    private Message cmdShutdown(String input) {
        if (connectionWorker.stopServer(input)) {
            return new Message(OK_BYE);
        }
        return new Message(ERROR, "unauthorized");
    }

    private Message cmdBye() {
        return new Message(BYE);
    }

    private Message cmdReverse(String input) {
        StringBuilder stringBuilder = new StringBuilder(255);
        for (int i = input.length() - 1; i >= 0; i--) {
            stringBuilder.append(input.charAt(i));
        }

        return new Message(OK, stringBuilder.toString());
    }

    private Message cmdUppercase(String input) {
        input = input.toUpperCase();
        return new Message(OK, input);
    }

    private Message cmdLowercase(String input) {
        input = input.toLowerCase();
        return new Message(OK, input);
    }
}
