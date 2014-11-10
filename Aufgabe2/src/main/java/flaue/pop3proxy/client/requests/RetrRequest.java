package flaue.pop3proxy.client.requests;

/**
 * Created by florian on 10.11.14.
 */
public class RetrRequest extends AbstractRequest {

    public static final String COMMAND = "RETR";

    public RetrRequest(String index) {
        super(COMMAND, index);
    }
}
