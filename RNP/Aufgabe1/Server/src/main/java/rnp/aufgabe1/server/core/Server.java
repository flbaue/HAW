/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core;

import rnp.aufgabe1.server.core.models.Client;
import rnp.aufgabe1.server.core.models.IncomingMessage;
import rnp.aufgabe1.server.core.models.Message;
import rnp.aufgabe1.server.core.services.Broadcaster;
import rnp.aufgabe1.server.core.services.CommandProcessor;
import rnp.aufgabe1.server.core.services.Receiver;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Server implements Runnable {

    private final BlockingDeque<IncomingMessage> receiverQueue;
    private final BlockingDeque<Message> broadcasterQueue;
    private final Set<Client> clients;
    private final int port;
    private final String password;
    private Receiver receiver;
    private Broadcaster broadcaster;
    private CommandProcessor commandProcessor;
    private Thread commandProcessorThread;
    private Thread broadcasterThread;
    private Thread receiverThread;
    private boolean shutdown;

    public Server(int port, String password) {
        receiverQueue = new LinkedBlockingDeque<>();
        broadcasterQueue = new LinkedBlockingDeque<>();
        clients = new HashSet<>();
        shutdown = false;
        this.password = password;
        this.port = port;
    }

    @Override
    public void run() {
        receiver = new Receiver(receiverQueue, port, clients);
        receiverThread = new Thread(receiver);
        receiverThread.setName("receiverThread");
        receiverThread.start();

        commandProcessor = new CommandProcessor(receiverQueue, broadcasterQueue, clients, password, this);
        commandProcessorThread = new Thread(commandProcessor);
        commandProcessorThread.setName("commandProcessorThread");
        commandProcessorThread.start();

        broadcaster = new Broadcaster(broadcasterQueue, clients, this);
        broadcasterThread = new Thread(broadcaster);
        broadcasterThread.setName("broadcasterThread");
        broadcasterThread.start();

        waitForShutdown();
    }

    private void waitForShutdown() {
        while (isAlive()) {
            if (shutdown) {
                waitForTimeout(60);
                shutdown();
            } else {
                ServerUtils.sleep(2000);
            }
        }
        System.out.println("Server: Server is off");
        System.out.println("Server: Goodbye.");
    }

    private void waitForTimeout(int timeout) {
        while (timeout > 0 && isAlive()) {
            timeout--;
            ServerUtils.sleep(1000);
        }
    }

    public void prepareShutdown() {
        System.out.println("Server: preparing shutdown");
        receiver.shutdown();
        commandProcessor.shutdown();
        broadcaster.shutdown();
        shutdown = true;
    }

    public void shutdown() {
        if (isReceiverThreadAlive()) {
            receiver.closeSocket();
        }
        if (isProcessorThreadAlive()) {
            commandProcessorThread.interrupt();
        }
        if (isBroadcasterThreadAlive()) {
            broadcasterThread.interrupt();
        }
    }

    public boolean isAlive() {
        return isReceiverThreadAlive() || isProcessorThreadAlive() || isBroadcasterThreadAlive();
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

    public boolean isProcessorThreadAlive() {
        return commandProcessorThread.isAlive();
    }

    public boolean isReceiverThreadAlive() {
        return receiverThread.isAlive();
    }

    public boolean isBroadcasterThreadAlive() {
        return broadcasterThread.isAlive();
    }


}
