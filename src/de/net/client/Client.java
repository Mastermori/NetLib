package de.net.client;

import de.net.interfaces.Receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    protected BufferedReader in;
    protected PrintWriter out;
    protected Thread thread;
    protected Receiver receiver;
    protected Socket s;

    public Client(String ip, int port, Receiver receiver) throws IOException {
        this(new Socket(ip, port), receiver);
    }

    public Client(Socket s, Receiver receiver) {
        this.s = s;
        try {
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.receiver = receiver;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (!s.isClosed()) {
            String line;
            try {
                while (!in.ready()) {
                    Thread.sleep(200);
                    if (s.isClosed()) return;
                }
                if (!(line = in.readLine()).equals("")) {
                    receiver.receive(line, this);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        out.println(msg);
    }

    public void send(String[] msg, String separator) {
        String finalMsg = "";
        for (String s : msg) {
            finalMsg += s + separator;
        }
        finalMsg = finalMsg.substring(0, finalMsg.length() - separator.length());
        System.out.println("Sent: " + finalMsg);
        send(finalMsg);
    }

    public synchronized void disconnect(){
        send("disconnect");
        close();
    }

    public synchronized void close() {
        try {
            if (s.isConnected()) {
                s.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
