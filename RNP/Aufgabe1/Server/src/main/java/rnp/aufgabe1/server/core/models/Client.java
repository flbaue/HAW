/*
 * Florian Bauer
 * flbaue@posteo.de
 * Copyright (c) 2014.
 */

package rnp.aufgabe1.server.core.models;

/**
 * Created by Florian Bauer on 06.10.14. flbaue@posteo.de
 */
public class Client {
    private final String host;
    private final int portOut;
    private int portIn;

    public Client(final String host, final int portOut) {
        this.host = host;
        this.portOut = portOut;
    }

    public void setPortIn(int portIn) {
        this.portIn = portIn;
    }

    public int getPortIn() {
        return portIn;
    }

    public int getPortOut() {
        return portOut;
    }

    public String getHost() {
        return host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (portOut != client.portOut) return false;
        if (!host.equals(client.host)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + portOut;
        return result;
    }
}
