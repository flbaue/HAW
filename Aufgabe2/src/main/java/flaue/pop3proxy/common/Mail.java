package flaue.pop3proxy.common;

/**
 * Created by flbaue on 09.11.14.
 */
public class Mail {

    private final String mail;
    private final String uid;
    private boolean delete;

    public Mail(String s, String uid) {
        this.mail = s;
        this.uid = uid;
    }

    public String getMail() {
        return mail;
    }

    public String getUid() {
        return uid;
    }

    public void delete() {
        delete = true;
    }

    public boolean isMarkedForDeletion() {
        return delete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mail mail = (Mail) o;

        if (uid != null ? !uid.equals(mail.uid) : mail.uid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
