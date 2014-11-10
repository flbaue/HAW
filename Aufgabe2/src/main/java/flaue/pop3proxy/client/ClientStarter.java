package flaue.pop3proxy.client;

import flaue.pop3proxy.common.Account;

import java.io.IOException;

/**
 * Created by flbaue on 08.11.14.
 */
public class ClientStarter {
    public static void main(String[] args) throws IOException {
        Account account = new Account("pop.gmx.net",995,"flo.bauer@gmx.net","olla85FLOW");
        Pop3Client pop3Client = new Pop3Client(account);
//        pop3Client.connect();
//        pop3Client.authorize();
//        pop3Client.list();
    }
}
