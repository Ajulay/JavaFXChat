package com.ajulay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    ServerSocket serverSocket = null;
    private Vector<ClientHandler> clients;

    public Server() {

        try {
            SQLHandler.connect();
            serverSocket =  new ServerSocket(8189);
            clients = new Vector<ClientHandler>();
            System.out.println("Server starting...");
            while(true){

            Socket socket = serverSocket.accept();
            System.out.println("connect...");
            new ClientHandler(this, socket);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            SQLHandler.disconnect();
        }
    }
    public void broadcastMsg(String str){
        for (ClientHandler cl: clients) {
            cl.sendMsg(str);
        }
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcastMsg(clientHandler.getNick() + " is off-line...");
    }
    public void subscribe(ClientHandler clientHandler){
        broadcastMsg(clientHandler.getNick() + " is on-line...");
        clients.add(clientHandler);
    }

    public boolean isNickIsBusy(String nick){
        for (ClientHandler ch: clients) {
            if (ch.getNick().equals(nick)) {
                return true;
            }
        }
        return false;}

    public Vector<ClientHandler> getClients() {
        return clients;
    }
}
