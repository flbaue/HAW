package flaue.pop3proxy.client.responses;

/**
 * Created by flbaue on 08.11.14.
 */
public class Responses {

    public static final String PREFIX_OK = "+OK";
    public static final String PREFIX_ERR = "-ERR";

    public static void requireOk(final Response response) {
        if (!(response instanceof OkResponse)) {
            throw new RuntimeException("Response required: OkResponse actual:" + response.getClass().getName() + " " +
                    response.getPayload());
        }
    }

    public static Response createResponse(final String text) {
        Response response;
        if (text.startsWith(PREFIX_OK)) {
            String message = text.substring(PREFIX_OK.length()).trim();
            if (message.isEmpty()) {
                response = new OkResponse();
            } else {
                response = new OkResponse(message);
            }
        } else if (text.startsWith(PREFIX_ERR)) {
            String message = text.substring(PREFIX_ERR.length()).trim();
            if (message.isEmpty()) {
                response = new ErrResponse();
            } else {
                response = new ErrResponse(message);
            }
        } else {
            throw new IllegalArgumentException("Response cannot be created from string '" + text + "'");
        }
        return response;
    }
}
