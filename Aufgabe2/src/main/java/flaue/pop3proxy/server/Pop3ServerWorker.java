package flaue.pop3proxy.server;

import flaue.pop3proxy.common.Pop3States;
import flaue.pop3proxy.mailstore.MailStore;

import java.io.*;
import java.net.Socket;

/**
 * Created by florian on 16.11.14.
 */
public class Pop3ServerWorker implements Runnable {
    private static final int READ = 0;
    private static final int LINE_END = 1;
    private static int instances = 0;

    private final String name;
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Pop3States state;
    private MailStore mailStore;
    private CommandProcessor commandProcessor;
    private boolean quit = false;
    private ServerThread server;

    public Pop3ServerWorker(Socket socket, MailStore mailStore, ServerThread server) {
        this.socket = socket;
        name = this.getClass().getName() + " " + ++instances;
        state = Pop3States.DISCONNECTED;
        commandProcessor = new CommandProcessor(mailStore, this);
        this.mailStore = mailStore;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open streams", e);
        }
        try {
            sendOK();
            while (!quit) {
                String input = readInput();
                String output = processInput(input);
                writeOutput(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.closeConnection(this);
    }

    private void sendOK() {
        try {
            out.write("+OK\t\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot send +OK", e);
        }
    }

    private void writeOutput(String output) {
        try {
            out.write(output);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to stream", e);
        }
    }

    private String processInput(String input) {
        return commandProcessor.process(input);

    }

    private String readInput() {
        int readState = READ;
        String input = "";
        char c = 0;
        while (!(c == '\n' && readState == LINE_END)) {
            try {
                int i = in.read();
                c = (char) i;
                if (readState == LINE_END && c != '\n') {
                    throw new RuntimeException("Illegal Command");
                }
                if (i != -1) {
                    input += c;
                }
                if (c == '\r') {
                    readState = LINE_END;
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot read from stream", e);
            }
        }
        return input.trim();
    }

    public String getName() {
        return name;
    }

    public void quit() {
        quit = true;
    }
}
