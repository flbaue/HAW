/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core.services;

import rnp.aufgabe1.server.core.Server;
import rnp.aufgabe1.server.core.ServerUtils;
import rnp.aufgabe1.server.core.models.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Broadcaster implements Runnable {

    private final BlockingDeque<Message> messageQueue;

    private final Set<Client> clients;
    private final Server server;
    private boolean shutdown;

    public Broadcaster(final BlockingDeque<Message> messageQueue, final Set<Client> clients, final Server server) {
        this.server = server;
        this.messageQueue = messageQueue;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageQueue.takeFirst();
                sendToReceiver(message);

                if (shutdown && messageQueue.isEmpty() && !server.isBroadcasterThreadAlive()) {
                    Thread.currentThread().interrupt();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Server: BroadcasterThread is off");
    }

    private void sendToReceiver(Message message) {
        Client client = message.getClient();
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(client.getHost(), client.getPortIn());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            writer.write(message.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ServerUtils.closeSocketSafely(clientSocket);
        }
    }

    public void shutdown() {
        shutdown = true;
    }
}
