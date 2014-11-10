package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.common.Mail;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void storeMail(Account account, Mail mail) {
        stores.get(account).storeMail(mail);
    }


    public List<Mail> getMails(Account account) {
        return stores.get(account).getMails();
    }


}
