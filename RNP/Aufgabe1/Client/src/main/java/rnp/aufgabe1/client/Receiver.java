/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by flbaue on 12.10.14.
 */
public class Receiver implements Runnable {
    private final int serverPort;

    public Receiver(final int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                char[] buffer = new char[255];
                int i = reader.read(buffer);
                if (i < 0 || i >= 255) {
                    //((error))
                }
                StringWriter stringWriter = new StringWriter();
                stringWriter.write(buffer);
                String response = stringWriter.toString();
                System.out.println("Server: " + response);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
