package flaue.pop3proxy.client.requests;

/**
 * Created by flbaue on 09.11.14.
 */
public abstract class AbstractRequest implements Request {

    private final String command;
    private final String payload;

    protected AbstractRequest(final String command, final String payload) {
        this.command = command;
        this.payload = payload;
    }

    protected AbstractRequest(final String command) {
        this(command, null);
    }

    @Override
    public String getPayload() {
        return payload != null ? payload : EMPTY_PAYLOAD;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public String toString() {
        return getCommand() + " " + getPayload();
    }

    public String toStringWithLineEnd() {
        return toString() + LINE_END;
    }
}
