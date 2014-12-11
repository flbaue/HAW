package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.common.Mail;

import java.util.*;

/**
 * Created by flbaue on 09.11.14.
 */
public class MailStore {

    private final Map<Account, MailDB> stores;

    public MailStore() {
        stores = new HashMap<>();
    }

    public void addStore(Account account, MailDB store) {
        if (!stores.containsKey(account)) {
            stores.put(account, store);
        }
    }

    public void addStore(Account account) {
        if (!stores.containsKey(account)) {
            MailDB mailDB = new InMemoryMailDB();
            stores.put(account, mailDB);
        }
    }

    public Mail storeMail(Account account, Mail mail) {
        return stores.get(account).storeMail(mail);
    }


    public List<Mail> getMails(Account account) {
        return stores.get(account).getMails();
    }

    public Set<Account> getAccounts() {
        return Collections.unmodifiableSet(stores.keySet());
    }

    public boolean hasAccount(String username) {
        for (Account account : stores.keySet()) {
            if (account.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean authorize(String username, String password) {
        for (Account account : stores.keySet()) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public List<Mail> getMails(String username, String password) {
        for (Account account : stores.keySet()) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return stores.get(account).getMails();
            }
        }
        return null;
    }

    public void deleteMarkedMails(String username, String password) {
        for (Account account : stores.keySet()) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                MailDB mailDB = stores.get(account);
                mailDB.deleteMarkedMails();
                break;
            }
        }
    }
}
