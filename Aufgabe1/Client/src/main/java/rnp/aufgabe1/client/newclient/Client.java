/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.client.newclient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by flbaue on 13.10.14.
 */
public class Client implements Runnable {

    private int serverPort;
    private String serverHost;

    public Client(int serverPort, String serverHost) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        try (Socket socket = new Socket(serverHost, serverPort)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            System.out.printf(">");
            String input;
            while (!(input = readFromConsole()).equals("exit")) {
                if (!isValid(input)) {
                    System.out.println("Input was not valid");
                    continue;
                }

                out.write(input + "\n");
                out.flush();
                System.out.print(readResponseFromServer(in));
                System.out.print(">");
            }

            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readResponseFromServer(Reader in) throws IOException {
        char[] buffer = new char[255];
        in.read(buffer);
        String input = String.valueOf(buffer);
        int end = input.indexOf("\u0000");
        return input.substring(0, end);
    }

    private boolean isValid(String input) {
        //TODO: more validation
        return input.length() < 255;
    }

    private String readFromConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
