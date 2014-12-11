package flaue.pop3proxy.common;

/**
 * Created by flbaue on 09.11.14.
 */
public class Mail {

    private final String content;
    private final String uid;
    private boolean delete = false;

    public Mail(String content, String uid) {
        this.content = content;
        this.uid = uid;
    }

    public Mail(String content) {
        this.content = content;
        this.uid = null;
    }

    public String getContent() {
        return content;
    }

    public String getUid() {
        return uid;
    }

    public void delete() {
        delete = true;
    }

    public void restore() {
        delete = false;
    }

    public boolean isMarkedForDeletion() {
        return delete;
    }

    @Override
    public String toString() {
        return uid;
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
