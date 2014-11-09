package flaue.pop3proxy.client.requests;

/**
 * Created by flbaue on 09.11.14.
 */
public interface Request {

    public static final String LINE_END = "\r\n";
    public static final String EMPTY_PAYLOAD = "";

    String getCommand();

    String getPayload();

    String toStringWithLineEnd();
}
