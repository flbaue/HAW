/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.experiment;

/**
 * Created by flbaue on 13.10.14.
 */
public class Starter {

    public static void main(String[] args) {

        ExServer server = new ExServer(8080);
        new Thread(server).start();

        try {
            Thread.sleep(20 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping Server");
        //server.stop();
    }
}
