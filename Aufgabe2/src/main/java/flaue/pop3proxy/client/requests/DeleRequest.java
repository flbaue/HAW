package flaue.pop3proxy.client.requests;

/**
 * Created by florian on 10.11.14.
 */
public class DeleRequest extends AbstractRequest {

    public static final String COMMAND = "DELE";

    public DeleRequest(String index) {
        super(COMMAND, index);
    }
}
