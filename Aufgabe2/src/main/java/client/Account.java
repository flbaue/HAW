package client;

/**
 * Created by flbaue on 08.11.14.
 */
public class Account {

    private String server;
    private int port;
    private String username;
    private String password;

    public Account(String server, int port, String username, String password) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
