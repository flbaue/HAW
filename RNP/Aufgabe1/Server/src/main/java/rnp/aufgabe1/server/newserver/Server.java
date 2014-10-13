/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.newserver;

import rnp.aufgabe1.server.oldserver.core.ServerUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by flbaue on 13.10.14.
 */
public class Server implements Runnable {

    private String secretToken;
    private int serverPort = 8080;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Map<ConnectionWorker, Thread> connections = new HashMap<>();


    public Server(String secretToken) {
        this.secretToken = secretToken;
    }

    public Server(String secretToken, int serverPort) {
        this.secretToken = secretToken;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (isStopped) {
                    break;
                } else {
                    throw new RuntimeException("Error accepting client connection", e);
                }
            }

            if (isStopped()) {
                break;
            }

            ConnectionWorker connection = new ConnectionWorker(clientSocket, this);
            Thread connectionThread = new Thread(connection);
            connectionThread.setName("connectionWorker" + connection.instance);
            connectionThread.start();
            connections.put(connection, connectionThread);
        }

        closeConnectionsWithTimeout();

        System.out.println("Server stopped.");
    }

    private void closeConnectionsWithTimeout() {
        int timeout = 60000;
        int sleep = 1000;
        while (!connections.isEmpty() && timeout <= 0) {
            ServerUtils.sleep(sleep);
            timeout -= sleep;
        }
        if (!connections.isEmpty()) {
            ServerUtils.closeServerSocketSafely(serverSocket);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

    private boolean isStopped() {
        return this.isStopped;
    }

    private void stop() {
        this.isStopped = true;
    }

    public boolean stop(String secretToken) {
        if (this.secretToken.equals(secretToken)) {
            stop();
            return true;
        }
        return false;
    }

    public void removeConnection(ConnectionWorker connection) {
        connections.remove(connection);
    }
}