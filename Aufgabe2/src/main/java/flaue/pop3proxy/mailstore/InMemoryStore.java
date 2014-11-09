package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Mail;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * Created by flbaue on 09.11.14.
 */
class InMemoryStore implements MailDB {

    private final Set<Mail> mails;

    InMemoryStore() {
        this.mails = new HashSet<>();
    }

    public void storeMail(Mail mail) {
        if (mail.getUid() != null && !mail.getUid().isEmpty()) {
            // TODO nothing
        } else {
            mails.add(new Mail(mail.getMail(), getFreeUid()));
        }
    }

    @Override
    public List<Mail> getMails() {
        return new ArrayList<>(mails);
    }


    String getFreeUid() {
        Random rnd = new Random();
        byte[] bytes = new byte[70];
        rnd.nextBytes(bytes);
        String uid = DatatypeConverter.printHexBinary(bytes);

        while (mails.contains(new Mail("", uid))) {
            rnd.nextBytes(bytes);
            uid = DatatypeConverter.printHexBinary(bytes);
        }
        return uid;
    }
}
