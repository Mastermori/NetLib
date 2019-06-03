package de.net.interfaces;

import de.net.client.Client;

public interface Receiver {

    void receive(String msg, Client c);

}
