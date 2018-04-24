package com.ajulay;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegistrationController {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    TextField login, password, nick;
    @FXML
    Label result;
    public void tryToRegister() {

        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", 8189);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("/registration " + login.getText() + " " + password.getText() + " " + nick.getText());
                String answer = in.readUTF();
                result.setText(answer);

            }
        } catch (IOException e) {
            System.out.println("reading error...");
            
        } finally {

            try {
                socket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
