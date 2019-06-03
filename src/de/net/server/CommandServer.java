package de.net.server;

import de.net.client.Client;
import de.net.interfaces.Command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CommandServer extends Server {

    protected HashMap<String, Command> commands;
    protected String separator;

    public CommandServer(int port) {
        this(port, -1);
    }

    public CommandServer(int port, int maxClients) {
        this(port, maxClients, "\\.");
    }

    public CommandServer(int port, int maxClients, String separator) {
        super(port, maxClients);
        commands = new HashMap<>();
        this.separator = separator;
    }

    public void loadStandardCommands() {
        addCommand("disconnect", (c, args) -> disconnectClient(c, false, true));
        addCommand("echo", (c, args) -> {
            for (Client client : clients)
                if (!client.equals(c)) client.send((String[]) args.toArray(), separator);
        });
        addCommand("echoNoIgnore", (c, args) -> {
            for (Client client : clients)
                client.send((String[]) args.toArray(), separator);
        });
    }

    public void addCommand(String id, Command com) {
        commands.put(id, com);
    }

    public void removeCommand(String id) {
        commands.remove(id);
    }

    @Override
    public synchronized void receive(String msg, Client c) {
        List<String> splitMsg = new LinkedList<>(Arrays.asList(msg.split(separator)));
        String index = splitMsg.remove(0);
        commands.get(index).execute(c, splitMsg);
    }
}
