package flaue.pop3proxy.client;

import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.mailstore.MailStore;

import java.io.IOException;
import java.util.Set;
import java.util.TimerTask;

/**
 * Created by florian on 15.11.14.
 */
public class ClientThread extends TimerTask {

    private Set<Pop3Client> pop3ClientSet;

    public ClientThread(MailStore mailStore) {
        for (Account account : mailStore.getAccounts()) {
            pop3ClientSet.add(new Pop3Client(account, mailStore));
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            for (Pop3Client pop3Client : pop3ClientSet) {
                try {
                    pop3Client.fetchMails();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
