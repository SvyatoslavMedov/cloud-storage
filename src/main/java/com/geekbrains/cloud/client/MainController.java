package com.geekbrains.cloud.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public TextField clientPath;
    public TextField serverPath;
    public ListView <String> clientView;
    public ListView <String> serverView;
    private File currentDirectory;

    private DataInputStream is;
    private DataOutputStream os;

    //Platform.runLater(() -> {})
    private void updateClientView(){
        Platform.runLater(()-> {
            clientPath.setText(currentDirectory.getAbsolutePath());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems()
                    .addAll(currentDirectory.list());
        });
    }

    public void download(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) {
    }

    private void initNetwork() {
        try {
            Socket socket = new Socket("localhost",8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDirectory = new File(System.getProperty("user.home"));



        //run in FX Thread
        //:: - method reference
        updateClientView();
        clientView.setOnMouseClicked(e-> {
            if(e.getClickCount()==2) {
                String item = clientView.getSelectionModel().getSelectedItem();
                if (item.equals("...")){
                    currentDirectory = currentDirectory.getParentFile();
                    updateClientView();
                }else{
                    File selected = currentDirectory.toPath().resolve(item).toFile();
                    if(selected.isDirectory()){
                        currentDirectory = selected;
                        updateClientView();
                    }
                }
            }
        });
    }
}
