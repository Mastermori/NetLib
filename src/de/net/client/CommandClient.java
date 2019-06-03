package de.net.client;

import de.net.interfaces.Command;
import de.net.interfaces.CommandReceiver;
import de.net.interfaces.Receiver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CommandClient implements Receiver {

    protected HashMap<String, Command> commands;
    protected String separator;
    private Client client;
    protected CommandReceiver cr;

    public CommandClient(String ip, int port, String separator) throws IOException {
        this(ip, port, separator, null);
    }

    public CommandClient(String ip, int port, String separator, CommandReceiver cr) throws IOException {
        client = new Client(ip, port, this);
        commands = new HashMap<>();
        this.separator = separator;
        this.cr = cr;
    }

    /**
     * Adds the following commands:
     * disconnect - closes the socket and streams
     * If a CommandReceiver is provided
     * setVar [varID] [varValue] - sets the variable with name "varID" to "varValue", needs to be implemented in CommandReceiver
     * setVars [varID1] [varValue1] [varID2] [varValue2] ... - sets all provided variables with setVar (see above)
     */
    public void loadStandardCommands() {
        addCommand("disconnect", (c, args) -> client.close());
        if (cr != null) {
            addCommand("setVar", (c, args) -> cr.setVar(args.get(0), args.get(1)));
            addCommand("setVars", (c, args) -> {
                while(!args.isEmpty()){
                    String varID = args.remove(0);
                    String varValue = args.remove(0);
                    cr.setVar(varID, varValue);
                }
            });
        }
    }

    public void addCommand(String id, Command command) {
        commands.put(id, command);
    }

    public void removeCommand(String id) {
        commands.remove(id);
    }

    @Override
    public void receive(String msg, Client c) {
        List<String> splitMsg = new LinkedList<>(Arrays.asList(msg.split(separator)));
        String index = splitMsg.remove(0);
        commands.get(index).execute(c, splitMsg);
    }

}
