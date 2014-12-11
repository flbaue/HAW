package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Mail;

import java.util.*;

/**
 * Created by flbaue on 09.11.14.
 */
public class InMemoryMailDB implements MailDB {

    private final Set<Mail> mails;
    private final Random rand = new Random();

    InMemoryMailDB() {
        this.mails = new HashSet<>();
    }

    public Mail storeMail(Mail mail) {
        if (mail.getUid() == null || mail.getUid().isEmpty()) {
            Mail newMail = new Mail(mail.getContent(), getFreeUid());
            mails.add(newMail);
            return newMail;
        }
        return null;
    }

    @Override
    public List<Mail> getMails() {
        return new ArrayList<>(mails);
    }

    @Override
    public void deleteMarkedMails() {
        Iterator<Mail> iterator = mails.iterator();
        while (iterator.hasNext()) {
            Mail mail = iterator.next();
            if (mail.isMarkedForDeletion()) {
                iterator.remove();
            }
        }
    }

    String getFreeUid() {
        int max_attempts = 10;
        int attempt = 1;
        int minimum = 0x21;
        int maximum = 0x7E;
        String uid = "";

        char c = (char) (rand.nextInt((maximum - minimum) + 1) + minimum);

        uid += c;
        while (mails.contains(new Mail("", uid)) && attempt <= max_attempts) {
            if (uid.length() == 70) {
                uid = "";
                attempt++;
            }
            c = (char) (rand.nextInt((maximum - minimum) + 1) + minimum);
            uid += c;
        }
        return uid;
    }
}
