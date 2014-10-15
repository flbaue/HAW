/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.oldserver.core.models;

import rnp.aufgabe1.server.oldserver.core.Command;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Message {

    private final String text;
    private final Command command;
    private final Client client;

    public Message(final Command command, final String text, final Client client) {
        this.client = client;
        this.command = command;
        this.text = text;
    }

    public Command getCommand() {
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
