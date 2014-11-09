package flaue.pop3proxy.client.requests;

/**
 * Created by flbaue on 09.11.14.
 */
public class PassRequest extends AbstractRequest {
    public static final String COMMAND = "USER";

    public PassRequest(final String password) {
        super(COMMAND, password);
    }
}
