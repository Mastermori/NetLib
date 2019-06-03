package de.net.server;

import de.net.client.Client;
import de.net.interfaces.Receiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class Server implements Receiver, Runnable {

    protected List<Client> clients;
    protected ServerSocket serverSocket;
    protected boolean listening;
    protected Thread thread;
    protected int maxClients;

    public Server(int port, int maxClients) {
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listening = true;
        this.maxClients = maxClients;
        thread = new Thread(this);
        thread.start();

    }

    public synchronized void disconnectClient(Client client, boolean sendDisconnect, boolean removeClient) {
        System.out.println("Disconnected Client");
        if (sendDisconnect)
            client.send("disconnect");
        client.close();
        if (removeClient)
            clients.remove(client);
    }

    @Override
    public void run() {
        try {
            while (listening) {
                if (clients.size() < maxClients || maxClients < 0) {
                    Socket cs = serverSocket.accept();
                    System.out.println("Connected to: " + cs.getInetAddress() + ":" + cs.getPort());
                    Client c = new Client(cs, this);
                    clients.add(c);
                    onClientConnect(c);
                } else {
                    Thread.sleep(200);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void onClientConnect(Client client){
        
    }

    public int getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public List<Client> getClients() {
        return clients;
    }

    public boolean isFull(){
        return clients.size() >= maxClients;
    }

    public boolean isListening() {
        return listening;
    }

    public void stop() {
        for (Client c : clients) {
            disconnectClient(c, true, false);
        }
        clients.clear();
        listening = false;
        try {
            serverSocket.close();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
