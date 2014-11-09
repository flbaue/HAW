package client.responses;

/**
 * Created by flbaue on 08.11.14.
 */
public interface Response {
    public static final String EMPTY_PAYLOAD = "";

    String getPayload();

    String getCommand();
}
