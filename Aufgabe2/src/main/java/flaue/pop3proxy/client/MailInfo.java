package flaue.pop3proxy.client;

import java.util.Objects;

/**
 * Created by florian on 10.11.14.
 */
public class MailInfo {

    private final String index;
    private final String octets;

    public MailInfo(String index, String octets) {
        if (index == null || index.isEmpty() || octets == null || octets.isEmpty()) {
            throw new IllegalArgumentException("Parameters must not be null or empty");
        }
        this.index = index;
        this.octets = octets;
    }

    public String getIndex() {
        return index;
    }

    public String getOctets() {
        return octets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MailInfo mailInfo = (MailInfo) o;

        if (!index.equals(mailInfo.index)) return false;
        if (!octets.equals(mailInfo.octets)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = index.hashCode();
        result = 31 * result + octets.hashCode();
        return result;
    }
}
