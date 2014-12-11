package flaue.pop3proxy.server;

import flaue.pop3proxy.mailstore.MailStore;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by florian on 15.11.14.
 */
public class ServerThread implements Runnable {

    private int port;
    private MailStore mailStore;
    private List<Pop3ServerWorker> connections = new LinkedList<>();

    public ServerThread(int port, MailStore mailStore) {
        this.port = port;
        this.mailStore = mailStore;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(30000);

                if (connections.size() < 5) {
                    Pop3ServerWorker pop3ServerWorker = new Pop3ServerWorker(socket, mailStore, this);
                    connections.add(pop3ServerWorker);
                    Thread pop3ServerWorkerThread = new Thread(pop3ServerWorker);
                    pop3ServerWorkerThread.setName(pop3ServerWorker.getName());
                    pop3ServerWorkerThread.start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Connection error", e);
        }
    }

    public void closeConnection(Pop3ServerWorker pop3ServerWorker) {
        connections.remove(pop3ServerWorker);
    }
}
