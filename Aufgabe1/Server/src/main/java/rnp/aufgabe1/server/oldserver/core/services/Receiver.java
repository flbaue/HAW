/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.oldserver.core.services;

import rnp.aufgabe1.server.oldserver.core.ServerUtils;
import rnp.aufgabe1.server.oldserver.core.models.Client;
import rnp.aufgabe1.server.oldserver.core.models.IncomingMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

/**
 * Receives messages from clients, encapsulates them with the client credentials to an IncomingMessage object, and adds
 * them to the messageQueue.
 * <p>
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
                // create server socket and listen on port till a client connects
                serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();

                // gather client credentials
                Client client = new Client(clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                boolean clientIsKnown = clients.contains(client);

                // if server is shutting down and the client is unregistered, it is ignored
                if (!shutdown || (shutdown && clientIsKnown)) {

                    // read exactly 255 byte from the client.
                    Writer writer = new StringWriter();
                    char[] buffer = new char[255];
                    Reader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                    int n = reader.read(buffer);
                    if (n == -1 || n >= 255) {
                        throw new IOException("Error while reading client data");
                    }
                    writer.write(buffer, 0, n);

                    // create IncomingMessage with the read 255 byte string and the client credentials. Add it to the
                    // messageQueue and close the connection.
                    IncomingMessage message = new IncomingMessage(writer.toString(), client);
                    messageQueue.addLast(message);
                    reader.close();
                }

                // if server is shutting down. no clients are left and no unprocessed messages are left,
                // this thread will be interrupted to stop.
                if (shutdown && clients.isEmpty() && messageQueue.size() == 0) {
                    Thread.currentThread().interrupt();
                }
            } catch (SocketException e) {
                // happens when we close the server socket by force. E.g. when the shutdown timeout is hit.
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // safely close the connection to wait for the next one.
                ServerUtils.closeServerSocketSafely(serverSocket);
            }
        }
        System.out.println("Server: ReceiverThread is off");
    }

    /**
     * Sets the shutdown mode for shutdown after no clients are left.
     */
    public void shutdown() {
        shutdown = true;
    }

    /**
     * Closes the server socket and forces the Receiver to immediately shutdown.
     */
    public void closeSocket() {
        ServerUtils.closeServerSocketSafely(serverSocket);
    }
}
