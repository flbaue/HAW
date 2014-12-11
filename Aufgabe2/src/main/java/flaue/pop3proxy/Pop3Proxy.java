package flaue.pop3proxy;

import flaue.pop3proxy.client.ClientThread;
import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.mailstore.MailStore;
import flaue.pop3proxy.server.ServerThread;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * Created by florian on 15.11.14.
 */
public class Pop3Proxy {

    public static final String EMPTY = "";
    public static final String INTERVAL_ARG = "interval=";
    public static final String ACCOUNTS_ARG = "accounts=";
    public static final String SERVER_PORT = "serverport=";
    public static final String ACCOUNT_TAG = "[ACCOUNTS]";
    public static final int PARSING_USER = 1;
    public static final int PARSER_FREE = 0;

    private long interval = 30000;
    private int serverport = 110;
    private Set<Account> accounts = new HashSet<>();

    public static void main(String[] args) {
        new Pop3Proxy().run(args);
    }

    private void run(String[] args) {
        setup(args);

        //store anlegen
        MailStore mailStore = new MailStore();
        createPostBoxes(accounts, mailStore);

        //Client Thread anlegen und starten
        ClientThread clientThread = new ClientThread(mailStore);
        Timer timer = new Timer();
        timer.schedule(clientThread, interval);

        //Server Thread anlegen und starten
        ServerThread serverThread = new ServerThread(serverport, mailStore);

    }

    private void createPostBoxes(Set<Account> accounts, MailStore mailStore) {
        for (Account account : accounts) {
            mailStore.addStore(account);
        }
    }

    private void setup(String[] args) {
        for (String arg : args) {
            if (arg.startsWith(INTERVAL_ARG)) {
                interval = Integer.parseInt(arg.substring(INTERVAL_ARG.length()));
            } else if (arg.startsWith(ACCOUNTS_ARG)) {
                String path = arg.substring(ACCOUNTS_ARG.length());
                accounts = readAccountsFromFile(path);
            } else if (arg.startsWith(SERVER_PORT)) {
                serverport = Integer.parseInt(arg.substring(SERVER_PORT.length()));
            }
        }
    }

    private Set<Account> readAccountsFromFile(String path) {
        Set<Account> accountSet = new HashSet<>();

        String user = EMPTY;
        String password = EMPTY;
        String server = EMPTY;
        String port = EMPTY;
        int state = PARSER_FREE;
        try {
            List<String> lines = Files.readAllLines(Paths.get(new URI(path)));
            for (String line : lines) {
                if (ACCOUNT_TAG.equals(line)) {
                    if (state != PARSER_FREE) {
                        throw new ParseException("Cannot parse accounts file", 0);
                    }
                    state = PARSING_USER;
                } else if (!line.isEmpty()) {
                    if (line.startsWith("user:")) {
                        user = line.split(":")[1];
                    } else if (line.startsWith("password:")) {
                        password = line.split(":")[1];
                    } else if (line.startsWith("server:")) {
                        server = line.split(":")[1];
                    } else if (line.startsWith("port:")) {
                        port = line.split(":")[1];
                    }
                }
                if (!(user.isEmpty() || password.isEmpty() || server.isEmpty() || port.isEmpty())) {
                    accountSet.add(new Account(server, Integer.parseInt(port), user, password));
                    user = EMPTY;
                    password = EMPTY;
                    server = EMPTY;
                    port = EMPTY;
                    state = PARSER_FREE;
                }
            }
        } catch (URISyntaxException | ParseException | IOException e) {
            throw new RuntimeException("Cannot read file", e);
        }


        return accountSet;
    }
}
