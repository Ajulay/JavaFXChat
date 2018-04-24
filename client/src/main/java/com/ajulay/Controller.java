package com.ajulay;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authorized;
    private String nick;


    public TextArea jta;
    public TextField jtf;
    public TextField loginField;
    public TextField passField;
    public HBox authPanel, msgPanel;
    public Button sendMsgBn, authBn;
    public Button regBn;


    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (this.authorized){
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
            jta.clear();
        }else
        {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            nick ="";
        }
    }



    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("localhost", 8189);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream()) ;

            loginField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty()){
                    authBn.setDisable(false);}
                else
                    authBn.setDisable(true);
            });

            jtf.textProperty().addListener((observable, oldValue, newValue) -> {
               if (!newValue.isEmpty()){
                   sendMsgBn.setDisable(false);}
                else
                   sendMsgBn.setDisable(true);
            });

        Thread t = new Thread(new Runnable() {
    public void run() {
        try {
            while(true){
                String aut = in.readUTF();
                if (aut.startsWith("/authok ") ){
                    nick = aut.split("\\s")[1];


                    break;
                }
                jta.appendText(aut);
            }
            setAuthorized(true);
        while (true) {
            String str = null;

            str = in.readUTF();

            jta.appendText(str + "\n");
        }
            } catch (IOException e) {
            System.out.println("disconnect with server...");

            }finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
});
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public void sendMsg(){
    try {
        String str = jtf.getText().trim();
        if (str.equals("")) {
            jtf.clear();
            return;}
        if (str.startsWith("/")){
            if (str.startsWith("/w ")){
                String [] prvt = str.split("\\s", 3);
                if (prvt.length ==3){
                out.writeUTF(prvt[0] +" " +prvt[1]+" " + prvt[2]);}
                jtf.clear();
                jtf.requestFocus();
                return;
            }
            if (str.startsWith("/ex")){

                out.writeUTF("/ex");
                jtf.clear();
                jtf.requestFocus();
                Stage stage = (Stage) msgPanel.getScene().getWindow();
                stage.close();
                return;
            }

        }
        out.writeUTF(nick +": "+ str);
        jtf.clear();
        jtf.requestFocus();
    } catch (IOException e) {

        e.printStackTrace();
    }
}
    public void sendAuth(ActionEvent actionEvent) {

        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void registerBtn() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/registration.fxml"));
            Parent root = loader.load();
            stage.setTitle("Chat: Sign up...");
            stage.setScene(new Scene(root, 400, 240));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
