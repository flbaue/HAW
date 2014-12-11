package flaue.pop3proxy.server;

import flaue.pop3proxy.client.requests.PassRequest;
import flaue.pop3proxy.client.requests.UserRequest;
import flaue.pop3proxy.common.Mail;
import flaue.pop3proxy.common.Pop3States;
import flaue.pop3proxy.mailstore.MailStore;

import java.util.List;

/**
 * Created by florian on 16.11.14.
 */
public class CommandProcessor {

    private static final String LINE_END = "\t\n";

    private final MailStore mailStore;
    private Pop3States state = Pop3States.AUTHORIZATION;
    private String username;
    private String password;
    private Pop3ServerWorker pop3ServerWorker;


    public CommandProcessor(MailStore mailStore, Pop3ServerWorker pop3ServerWorker) {
        this.mailStore = mailStore;
        this.pop3ServerWorker = pop3ServerWorker;
    }

    public String process(String input) {
        String output = "-ERR an error has occurred" + LINE_END;
        try {
            switch (state) {
                case DISCONNECTED:
                    ;
                    break;
                case AUTHORIZATION:
                    output = handleAuthorization(input);
                    break;
                case TRANSACTION:
                    output = handleTransaction(input);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return output;
    }

    private String handleTransaction(String input) {
        if (input.equals("STAT")) {
            List<Mail> mailList = mailStore.getMails(username, password);
            int mails = mailList.size();
            int octets = calcOctets(mailList);
            return "+OK " + String.valueOf(mails) + String.valueOf(octets) + LINE_END;
        } else if (input.equals("LIST")) {
            List<Mail> mailList = mailStore.getMails(username, password);
            int mails = mailList.size();
            int octets = calcOctets(mailList);
            String output = "+OK " + String.valueOf(mails) + " messages (" + String.valueOf(octets) + " octets)" + LINE_END;

            int i = 1;
            for (Mail mail : mailList) {
                if (!mail.isMarkedForDeletion()) {
                    output += String.valueOf(i) + String.valueOf(mail.getContent().getBytes().length) + LINE_END;
                }
                i += 1;
            }

            output += "." + LINE_END;
            return output;
        } else if (input.startsWith("LIST")) {

            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            List<Mail> mailList = mailStore.getMails(username, password);
            if (index < mailList.size()) {
                Mail mail = mailList.get(index);
                if (!mail.isMarkedForDeletion()) {
                    int octets = mail.getContent().getBytes().length;
                    return "+OK " + String.valueOf(index + 1) + " " + String.valueOf(octets) + LINE_END;
                } else {
                    return "-ERR message already deleted" + LINE_END;
                }
            } else {
                return "-ERR no such message, only " + mailList.size() + " messages in maildrop" + LINE_END;
            }
        } else if (input.startsWith("RETR")) {

            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            List<Mail> mailList = mailStore.getMails(username, password);
            if (index < mailList.size()) {
                Mail mail = mailList.get(index);
                if (!mail.isMarkedForDeletion()) {
                    int octets = mail.getContent().getBytes().length;
                    return "+OK " + octets + " octets" + LINE_END + mail.getContent() + LINE_END + "." + LINE_END;
                } else {
                    return "-ERR message already deleted" + LINE_END;
                }
            } else {
                return "-ERR no such message" + LINE_END;
            }
        } else if (input.startsWith("DELE")) {
            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            List<Mail> mailList = mailStore.getMails(username, password);
            if (index < mailList.size()) {
                Mail mail = mailList.get(index);
                if (mail.isMarkedForDeletion()) {
                    return "-ERR message already deleted" + LINE_END;
                } else {
                    mail.delete();
                    return "+OK message deleted" + LINE_END;
                }
            } else {
                return "-ERR no such message" + LINE_END;
            }
        } else if (input.equals("NOOP")) {
            return "+OK" + LINE_END;
        } else if (input.equals("RSET")) {
            List<Mail> mailList = mailStore.getMails(username, password);
            for (Mail mail : mailList) {
                mail.restore();
            }
            return "+OK maildrop has " + mailList.size() + " messages" + LINE_END;
        } else if (input.equals("UIDL")) {
            List<Mail> mailList = mailStore.getMails(username, password);
            int mails = mailList.size();
            int octets = calcOctets(mailList);
            String output = "+OK " + LINE_END;

            int i = 1;
            for (Mail mail : mailList) {
                if (!mail.isMarkedForDeletion()) {
                    output += String.valueOf(i) + " " + mail.getUid() + LINE_END;
                }
                i += 1;
            }

            output += "." + LINE_END;
            return output;
        } else if (input.startsWith("UIDL")) {
            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            List<Mail> mailList = mailStore.getMails(username, password);
            if (index < mailList.size()) {
                Mail mail = mailList.get(index);
                if (!mail.isMarkedForDeletion()) {
                    return "+OK " + String.valueOf(index + 1) + " " + mail.getUid() + LINE_END;
                } else {
                    return "-ERR message already deleted" + LINE_END;
                }
            } else {
                return "-ERR no such message, only " + mailList.size() + " messages in maildrop" + LINE_END;
            }
        } else if (input.equals("QUIT")) {
            mailStore.deleteMarkedMails(username, password);
            pop3ServerWorker.quit();
            return "+OK Bye" + LINE_END;
        } else {
            return "-ERR unknown command" + LINE_END;
        }

        //TODO catch exceptions, make methods, extract mailstore
    }

    private int calcOctets(List<Mail> mailList) {
        int octets = 0;
        for (Mail mail : mailList) {
            octets += mail.getContent().getBytes().length;
        }
        return octets;
    }

    private String handleAuthorization(String input) {
        if (input.startsWith(UserRequest.COMMAND)) {
            String user = input.split(" ")[1].trim();
            if (mailStore.hasAccount(user)) {
                username = user;
                return "+OK " + user + " is a valid mailbox" + LINE_END;
            } else {
                return "-ERR never heard of mailbox " + user + LINE_END;
            }
        } else if (input.startsWith(PassRequest.COMMAND)) {
            if (username == null || username.isEmpty()) {
                return "-ERR no username was given" + LINE_END;
            }
            String pass = input.split(" ")[1].trim();
            if (mailStore.authorize(username, pass)) {
                password = pass;
                state = Pop3States.TRANSACTION;
                return "+OK maildrop ready" + LINE_END;
            } else {
                return "-ERR invalid password" + LINE_END;
            }
        } else if (input.equals("QUIT")) {
            mailStore.deleteMarkedMails(username, password);
            pop3ServerWorker.quit();
            return "+OK Bye" + LINE_END;
        } else {
            return "-ERR authorization is now required" + LINE_END;
        }
    }
}
