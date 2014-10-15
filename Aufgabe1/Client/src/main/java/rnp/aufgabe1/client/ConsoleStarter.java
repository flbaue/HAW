/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.client;

/**
 * Created by flbaue on 13.10.14.
 */
public class ConsoleStarter {
    private int serverport;
    private String serverhost;

    public static void main(String[] args) {
        new ConsoleStarter().run(args);
    }

    private void run(String[] args) {
        setup(args);
        Thread clientThread = new Thread(new Client(serverport, serverhost));
        clientThread.start();
    }

    public void setup(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("serverport=")) {
                serverport = Integer.parseInt(arg.split("=")[1]);
            }
            if (arg.startsWith("serverhost=")) {
                serverhost = arg.split("=")[1];
            }
        }
    }
}
