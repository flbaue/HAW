package flaue.pop3proxy.common;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (port != account.port) return false;
        if (!password.equals(account.password)) return false;
        if (!server.equals(account.server)) return false;
        if (!username.equals(account.username)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = server.hashCode();
        result = 31 * result + port;
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
