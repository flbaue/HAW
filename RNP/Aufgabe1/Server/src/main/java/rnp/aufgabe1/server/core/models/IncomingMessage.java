/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core.models;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class IncomingMessage {

    private final String content;
    private final Client host;

    public IncomingMessage(final String content, final Client host){
        this.content = content;
        this.host = host;
    }

    public String getContent() {
        return content;
    }

    public Client getClient() {
        return host;
    }
}
