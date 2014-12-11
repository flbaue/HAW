package flaue.pop3proxy.server;

import flaue.pop3proxy.client.Pop3Client;
import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.mailstore.MailStore;

import java.io.IOException;

/**
 * Created by florian on 19.11.14.
 */
public class ServerStarter {

    public static void main(String[] args) {
        new ServerStarter().run();
    }

    private void run() {
        Account account = new Account("127.0.0.1", 22222, "tester", "tester");
        MailStore mailStore = new MailStore();
        mailStore.addStore(account);
        ServerThread serverThread = new ServerThread(account.getPort(), mailStore);
        Thread st = new Thread(serverThread);
        st.start();

        Pop3Client pop3Client = new Pop3Client(account,mailStore);
        try {
            pop3Client.fetchMails();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
