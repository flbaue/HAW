package client.experiments;

import client.Account;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * Created by flbaue on 08.11.14.
 */
public class Pop3client {
    public static void main(String[] args) {
        try {


            Account account = new Account("pop.gmx.net", 995, "flo.bauer@gmx.net", "olla85FLOW");

            // CONNECT
            SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket connection = ssf.createSocket(account.getServer(), account.getPort());
            //Socket connection = new Socket("haw-mailer.haw-hamburg.de", 995);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            System.out.println(in.readLine());

            //USER
            out.write("USER " + account.getUsername() + "\r\n");
            out.flush();
            System.out.println(in.readLine());

            //PASS
            out.write("PASS " + account.getPassword() + "\r\n");
            out.flush();
            System.out.println(in.readLine());

            //STAT
            out.write("STAT\r\n");
            out.flush();
            System.out.println(in.readLine());

            //LIST
            out.write("LIST\r\n");
            out.flush();
            String input = "";
            while(!input.equals(".")){
                input = in.readLine();
                System.out.println(input);
            }

            //RETR
            out.write("RETR 1\r\n");
            out.flush();
            input = "";
            while(!input.equals(".")){
                input = in.readLine();
                System.out.println(input);
            }

            //QUIT
            out.write("QUIT\r\n");
            out.flush();
            System.out.println(in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
