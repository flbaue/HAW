/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.newserver;

/**
 * Created by flbaue on 13.10.14.
 */
public class Message {
    private final Command command;
    private final String text;

    public Message(Command command, String text) {
        this.command = command;
        this.text = text;
    }

    public Message(Command command) {
        this.command = command;
        this.text = null;
    }

    public Command getCommand() {
        return command;
    }

    public String getText() {
        return (text == null) ? "" : text;
    }

    public String toString() {
        String result = command.name();
        if (text != null && !text.isEmpty()) {
            result += " " + text;
        }
        return result + "\n";
    }
}
