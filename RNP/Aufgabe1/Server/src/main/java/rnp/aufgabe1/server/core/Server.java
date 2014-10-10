/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Server {

    private final BlockingDeque<IncomingMessage> receiverQueue;
    private final BlockingDeque<Message> broadcasterQueue;
    private final Set<Client> clients;
    private Receiver receiver;
    private Broadcaster broadcaster;
    private CommandProcessor commandProcessor;
    private Thread commandProcessorThread;
    private Thread broadcasterThread;
    private Thread receiverThread;

    public Server() {
        receiverQueue = new LinkedBlockingDeque();
        broadcasterQueue = new LinkedBlockingDeque();
        clients = new HashSet<>();
    }

    public void start(int port, String password) {
        receiver = new Receiver(receiverQueue, port, clients);
        receiverThread = new Thread(receiver);
        receiverThread.setName("receiverThread");
        receiverThread.start();

        commandProcessor = new CommandProcessor(receiverQueue, broadcasterQueue, clients, password, this);
        commandProcessorThread = new Thread(commandProcessor);
        commandProcessorThread.setName("commandProcessorThread");
        commandProcessorThread.start();

        broadcaster = new Broadcaster(broadcasterQueue, clients);
        broadcasterThread = new Thread(broadcaster);
        broadcasterThread.setName("broadcasterThread");
        broadcasterThread.start();
    }

    public void shutdown() {
        int sleep = 1000;
        int timeout = sleep * 60;

        while (isAlive()) {
            if (timeout <= 0) {
                System.out.println("server shutdown timeout: remaining connections will be terminated");
                clients.clear();
            }

            if (clients.size() > 0) {
                receiver.shutdown();
                commandProcessor.shutdown();
                broadcaster.shutdown();
            } else {
                receiver.closeSocket();
                commandProcessorThread.interrupt();
                broadcasterThread.interrupt();
            }

            System.out.println(status());

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout -= sleep;
        }
    }

    public boolean isAlive() {
        return receiverThread.isAlive() || commandProcessorThread.isAlive() || broadcasterThread.isAlive();
    }

    public String status() {
        return "----------\n" + receiverThread.getName() + " is alive:" + receiverThread.isAlive() + ", " +
                "is interupted:" + receiverThread.isInterrupted() + "\n" +
                commandProcessorThread.getName() + " is alive:" + commandProcessorThread.isAlive() + ", " +
                "is interupted:" + commandProcessorThread.isInterrupted() + "\n" +
                broadcasterThread.getName() + " is alive:" + broadcasterThread.isAlive() + ", " +
                "is interupted:" + broadcasterThread.isInterrupted() + "\n" +
                "active clients:" + clients.size() + "\n" +
                "receiverQueue:" + receiverQueue.size() + "\n" +
                "broadcasterQueue:" + broadcasterQueue.size() +
                "----------\n";
    }

}
