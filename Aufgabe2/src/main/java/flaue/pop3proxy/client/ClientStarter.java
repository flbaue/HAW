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
        MailStore mailStore = new MailStore();
        Account account = new Account("pop.gmx.net", 995, "flo.bauer@gmx.net", "NtgvygAE6HkfsfF8f_zw");
        Pop3Client pop3Client = new Pop3Client(account, mailStore);

        pop3Client.fetchMails();
    }
}
