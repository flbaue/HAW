package client.requests;

/**
 * Created by flbaue on 09.11.14.
 */
public class ListRequest extends AbstractRequest {
    public static final String COMMAND = "LIST";

    public ListRequest(final String index) {
        super(COMMAND, index);
    }
    public ListRequest() {
        super(COMMAND);
    }
}
