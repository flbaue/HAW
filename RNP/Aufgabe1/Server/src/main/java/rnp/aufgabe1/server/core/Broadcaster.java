/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

import rnp.aufgabe1.server.ServerUtils;

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
    private boolean shutdown = false;

    public Broadcaster(BlockingDeque<Message> messageQueue, final Set<Client> clients) {
        this.messageQueue = messageQueue;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = messageQueue.takeFirst();
                sendToReceiver(message);

                if (shutdown && clients.isEmpty()) {
                    Thread.currentThread().interrupt();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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
