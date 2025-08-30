package com.mycompany.chatapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket s;
    private Server server;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    
    public ClientHandler(Socket s, Server server, ObjectInputStream in, ObjectOutputStream out) {
        this.s = s;
        this.server = server;
        this.in = in;
        this.out = out;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String ClientMessage = in.readUTF();
                System.out.println(ClientMessage);
                server.commandList(ClientMessage, this);
            }
        }
        catch (IOException ex) {
            System.out.println("Connection Disconnected");
        }
        finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (s != null) s.close();
            } catch (IOException e) {
                System.out.println("ClientHandler run\n" + e);
            }
        }
    }
    
    public ObjectOutputStream getOut() {
        return this.out;
    }
}
