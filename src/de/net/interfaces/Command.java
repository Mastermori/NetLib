package de.net.interfaces;

import de.net.client.Client;

import java.util.List;

public interface Command {

    void execute(Client c, List<String> args);

}
