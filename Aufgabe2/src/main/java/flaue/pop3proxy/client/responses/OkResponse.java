package flaue.pop3proxy.client.responses;

/**
 * Created by flbaue on 08.11.14.
 */
public class OkResponse extends AbstractResponse {

    public static final String COMMAND = "+OK";

    public OkResponse(final String payload) {
        super(COMMAND, payload);
    }

    public OkResponse() {
        super(COMMAND);
    }
}
