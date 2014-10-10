/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Message {

    private final String text;
    private final Commands command;
    private final Client client;

    public Message(final Commands command, final String text, final Client client) {
        this.client = client;
        this.command = command;
        this.text = text;
    }

    public Commands getCommand() {
        return this.command;
    }

    public Client getClient() {
        return client;
    }

    public String getText() {

        return this.text;
    }

    public String toString() {
        if (getText().length() > 0) {
            return getCommand() + " " + getText() + "\n";
        } else {
            return getCommand() + "\n";
        }
    }
}
