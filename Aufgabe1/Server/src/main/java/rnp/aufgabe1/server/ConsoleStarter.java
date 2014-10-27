/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server;

/**
 * Created by flbaue on 13.10.14.
 */
public class ConsoleStarter {
    private int serverport;
    private String secretToken;

    public static void main(String[] args) {
        new ConsoleStarter().run(args);
    }

    private void run(String[] args) {
        setup(args);
        Thread serverThread = new Thread(new Server(secretToken, serverport));
        serverThread.start();
    }

    public void setup(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("serverport=")) {
                serverport = Integer.parseInt(arg.split("=")[1]);
                System.out.println("secretport=" + serverport);
            }
            if (arg.startsWith("secrettoken=")) {
                secretToken = arg.split("=")[1];
                System.out.println("secrettoken=" + secretToken);
            }
        }
    }
}
