package client.responses;

/**
 * Created by flbaue on 08.11.14.
 */
public class ErrResponse extends AbstractResponse {

    public static final String COMMAND = "-ERR";

    public ErrResponse(String payload) {
        super(COMMAND, payload);
    }

    public ErrResponse() {
        super(COMMAND);
    }
}
