package flaue.pop3proxy.client.requests;

/**
 * Created by flbaue on 09.11.14.
 */
public class UserRequest extends AbstractRequest {

    public static final String COMMAND = "USER";

    public UserRequest(final String username) {
        super(COMMAND, username);
    }
}
