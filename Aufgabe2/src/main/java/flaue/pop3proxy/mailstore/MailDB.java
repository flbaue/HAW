package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Mail;

import java.util.List;

/**
 * Created by flbaue on 09.11.14.
 */
public interface MailDB {
    void storeMail(Mail mail);
    List<Mail> getMails();
}
