/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rnp.aufgabe1.server.Command.*;

/**
 * Created by flbaue on 13.10.14.
 */
public class CommandProcessor {

    // Pattern to validate the IncomingMessage content
    private final Pattern messagePattern = Pattern.compile("^[A-Z]+(\\u0020.+)?\\n");
    private static final String UNKNOWN_COMMAND = "Unknown command: ";
    public static final String UNAUTHORIZED = "Unauthorized";
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
            String text;
            if(space >= 0) {
                cmdName = input.substring(0, space);
                text = input.substring(space).trim();
            } else {
                cmdName = input.substring(0, input.length() - 1);
                text = "";
            }
            Command command;
            try {
                command = valueOf(cmdName);
                validateText(command,text);
            } catch (IllegalArgumentException e) {
                command = ERROR;
                if(e.getMessage().isEmpty()) {
                    int end = (input.length() < 200) ? input.length() : 230;
                    text = input.substring(0, end) + "...";
                } else {
                    text = e.getMessage();
                }
            }
            return new Message(command, text);
        } else {
            String result = input.trim();
            int end = (result.length() < 200) ? result.length() : 230;
            return new Message(ERROR, UNKNOWN_COMMAND + result.substring(0, end) + "...");
        }
    }

    private void validateText(Command command, String text) {
        if(command == REVERSE && text.isEmpty()){
            throw new IllegalArgumentException("string must not be empty");
        }
        if(command == LOWERCASE && text.isEmpty()){
            throw new IllegalArgumentException("string must not be empty");
        }
        if(command == UPPERCASE && text.isEmpty()){
            throw new IllegalArgumentException("string must not be empty");
        }
    }


    private Message cmdError(String input) {
        int end = (input.length() < 200) ? input.length() : 230;
        return new Message(ERROR, UNKNOWN_COMMAND + input.substring(0, end) + "...");
    }

    private Message cmdShutdown(String input) {
        if (connectionWorker.stopServer(input)) {
            return new Message(OK_BYE);
        }
        return new Message(ERROR, UNAUTHORIZED);
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
