package de.net.server;

import de.net.client.Client;

public class EchoServer extends Server {

    public EchoServer(int port) {
        this(port, -1);
    }

    public EchoServer(int port, int maxClients) {
        super(port, maxClients);
    }

    @Override
    public void receive(String msg, Client c) {
        System.out.println("Echoed: " + msg);
        for (Client client : clients)
            if (!client.equals(c)) client.send(msg);
    }

}
