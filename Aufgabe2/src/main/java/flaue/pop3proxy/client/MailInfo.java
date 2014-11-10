package flaue.pop3proxy.client;

/**
 * Created by florian on 10.11.14.
 */
public class MailInfo {

    private final int index;
    private final int octets;

    public MailInfo(int index, int octets) {
        this.index = index;
        this.octets = octets;
    }

    public int getIndex() {
        return index;
    }

    public int getOctets() {
        return octets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailInfo mailInfo = (MailInfo) o;

        if (index != mailInfo.index) return false;
        if (octets != mailInfo.octets) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + octets;
        return result;
    }
}
