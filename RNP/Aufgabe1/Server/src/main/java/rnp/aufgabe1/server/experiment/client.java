/*
 * Florian Bauer
 * florian.bauer@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.experiment;

import java.io.*;
import java.net.Socket;

/**
 * Created by flbaue on 13.10.14.
 */
public class client {

    public static void main(String[] args) throws IOException {

        int serverport = 8080;
        Socket socket = new Socket("127.0.0.1",serverport);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        writer.write("Test abc\n");
        writer.flush();

        String input;
        while((input = reader.readLine()) != null){
            System.out.println(input);
        }
    }
}
