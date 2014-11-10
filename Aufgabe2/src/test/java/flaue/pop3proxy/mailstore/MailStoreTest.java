package flaue.pop3proxy.mailstore;

import flaue.pop3proxy.common.Account;
import flaue.pop3proxy.common.Mail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MailStoreTest {

    MailStore mailStore;
    Account account1;
    Account account2;

    @Before
    public void setUp() throws Exception {
        account1 = new Account("gmx.net", 995, "test@gmx.net", "abc");
        account2 = new Account("web.de", 995, "test@web.de", "abc");
        mailStore = new MailStore();
        mailStore.addStore(account1, new InMemoryMailDB());
        mailStore.addStore(account2, new InMemoryMailDB());
    }

    @Test
    public void testAddStore() throws Exception {

    }

    @Test
    public void testStoreMail() throws Exception {
        mailStore.storeMail(account1, new Mail("a"));
        mailStore.storeMail(account1, new Mail("b"));
        mailStore.storeMail(account2, new Mail("c"));
        Assert.assertEquals(2, mailStore.getMails(account1).size());
        Assert.assertEquals(1, mailStore.getMails(account2).size());
    }

    @Test
    public void testGetMails() throws Exception {
        mailStore.storeMail(account1, new Mail("a"));
        mailStore.storeMail(account1, new Mail("b"));
        mailStore.storeMail(account2, new Mail("c"));
        Assert.assertEquals(2, mailStore.getMails(account1).size());
        Assert.assertEquals(1, mailStore.getMails(account2).size());

        List<Mail> mails1 = mailStore.getMails(account1);
        List<Mail> mails2 = mailStore.getMails(account2);

        Set<String> contents = new HashSet<>();
        contents.add("a");
        contents.add("b");

        contents.remove(mails1.get(0).getContent());
        contents.remove(mails1.get(1).getContent());
        Assert.assertTrue(contents.isEmpty());

        contents.add("c");
        contents.remove(mails2.get(0).getContent());
        Assert.assertTrue(contents.isEmpty());

        Set<String> uids = new HashSet<>();
        for (Mail mail : mails1) {
            String uid = mail.getUid();
            Assert.assertNotNull(uid);
            Assert.assertFalse(uid.isEmpty());
            Assert.assertFalse(uids.contains(uid));
            uids.add(uid);
        }
    }

    @Test
    public void testUids() throws Exception {
        for (int i = 0; i < 100000; i++) {
            mailStore.storeMail(account1, new Mail(String.valueOf(i)));
        }
        List<Mail> mails1 = mailStore.getMails(account1);
        Set<String> uids = new HashSet<>();
        for (Mail mail : mails1) {
            String uid = mail.getUid();
            Assert.assertNotNull(uid);
            Assert.assertFalse(uid.isEmpty());
            Assert.assertFalse(uids.contains(uid));
            uids.add(uid);
        }
    }
}