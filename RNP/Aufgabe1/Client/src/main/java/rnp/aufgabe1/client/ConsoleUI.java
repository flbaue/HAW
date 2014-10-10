/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.client;

/**
 * Created by Florian Bauer on 07.10.14. flbaue@posteo.de
 */
public class ConsoleUI {
    private int port;
    private String serverAddress;

    public static void main(final String[] args) {
        new ConsoleUI().run(args);
    }

    private void run(final String[] args) {
        parseArgs(args);
        if(!validateSetup()) {
            throw new IllegalArgumentException("serverAddress and/or port are not valid");
        }

        Client client = new Client(serverAddress,port);

        String message = "UPPERCASE abc\n";
        System.out.println("sending: '" + message + "'");
        client.sendMessage(message);
        System.out.println("done.");
    }

    private boolean validateSetup() {
        return port > 0 && serverAddress != null && !serverAddress.isEmpty();
    }

    private void parseArgs(final String[] args) {
        for (String arg : args) {
            if (arg.startsWith("port=")) {
                port = Integer.parseInt(arg.split("=")[1]);
            }
            if(arg.startsWith("server=")) {
                serverAddress = arg.split("=")[1];
            }
        }
    }
}
