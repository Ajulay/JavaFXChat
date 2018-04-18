package com.ajulay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ajulay on 20.03.2018.
 */
public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(this.socket.getInputStream());
            out = new DataOutputStream(this.socket.getOutputStream());


            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth ")) {
                            String[] ss = str.split("\\s");
                            String nick = SQLHandler.getNickByLoginPass(ss[1], ss[2]);

                            if (nick != null) {
                                if (server.isNickIsBusy(nick)) {
                                    out.writeUTF("Your nick has already activated...");
                                    continue;
                                }

                                out.writeUTF("/authok " + nick);
                                this.nick = nick;
                                server.subscribe(this);
                                break;
                            } else
                                out.writeUTF("Wrong login/password...");
                        }
                    }


                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.startsWith("/w ")) {
                                String[] pvt = str.split("\\s", 3);
                                for (ClientHandler ch : server.getClients()) {
                                    if (ch.getNick().equals(pvt[1])) {
                                        ch.sendMsg("Private message from "+ nick + ": " + pvt[2]);
                                        sendMsg("Private message for " + ch.getNick() + ": " + pvt[2]);
                                    }
                                      } continue;

                            }

                            if (str.startsWith("/ex") ){

                                break;
                            }

                        }


                        System.out.println(str);
                        server.broadcastMsg(str);

                    }

                } catch (IOException e) {

                   // e.printStackTrace();
                } finally {
                    System.out.println(nick + " disconnect...");
                    try {
                        socket.close();
                        in.close();
                        out.close();
                        server.unsubscribe(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNick() {
        return nick;
    }
}
