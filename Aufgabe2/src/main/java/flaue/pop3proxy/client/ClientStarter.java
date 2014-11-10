package flaue.pop3proxy.client;

import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.mailstore.InMemoryMailDB;
import flaue.pop3proxy.mailstore.MailStore;

import java.io.IOException;

/**
 * Created by flbaue on 08.11.14.
 */
public class ClientStarter {
    public static void main(String[] args) throws IOException {
        MailStore mailStore = new MailStore(InMemoryMailDB.class);
        Account account = new Account("pop.gmx.net", 995, "***", "***");
        Pop3Client pop3Client = new Pop3Client(account, mailStore);
//        pop3Client.connect();
//        pop3Client.authorize();
//        pop3Client.list();
    }
}
