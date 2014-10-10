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
    private final Client client;

    public IncomingMessage(final String content, final Client client){
        this.content = content;
        this.client = client;
    }

    public String getContent() {
        return content;
    }

    public Client getClient() {
        return client;
    }
}
