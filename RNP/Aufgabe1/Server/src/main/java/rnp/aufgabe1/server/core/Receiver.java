/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

import rnp.aufgabe1.server.ServerUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Receiver implements Runnable {

    private final int port;
    private final BlockingDeque<IncomingMessage> messageQueue;
    private final Set<Client> clients;
    private ServerSocket serverSocket;
    private boolean shutdown = false;


    public Receiver(final BlockingDeque<IncomingMessage> messageQueue, final int port, final Set<Client> clients) {
        this.messageQueue = messageQueue;
        this.port = port;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                boolean clientIsKnown = clients.contains(client);

                if(!shutdown || (shutdown && clientIsKnown)) {

                    Writer writer = new StringWriter();
                    char[] buffer = new char[255];
                    Reader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                    int n = reader.read(buffer);
                    if (n == -1 || n >= 255) {
                        //TODO error
                    }
                    writer.write(buffer, 0, n);

                    IncomingMessage message = new IncomingMessage(writer.toString(), client);
                    messageQueue.addLast(message);
                    reader.close();
                }

                if(shutdown && clients.isEmpty()) {
                    Thread.currentThread().interrupt();
                }

            } catch (SocketException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ServerUtils.closeServerSocketSafely(serverSocket);
            }
        }
    }

    public void shutdown() {
        shutdown = true;
    }

    public void closeSocket() {
        ServerUtils.closeServerSocketSafely(serverSocket);
    }
}
