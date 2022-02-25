package com.geekbrains.cloud.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    private File currentDirectory;

    public TextField clientPath;
    public TextField serverPath;
    public ChoiceBox <String> disks;
    public ListView <String> clientView;
    public ListView <String> serverView;


    //Platform.runLater(() -> {})
    private void updateClientView(){

        clientView.getItems().clear();
        clientView.getItems().add("...");
        clientView.getItems()
                .addAll(currentDirectory.list());

    }

    public void download(ActionEvent actionEvent) {
    }

    public void upload(ActionEvent actionEvent) {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDirectory = new File(System.getProperty("user.home"));
        Platform.runLater(this::updateClientView);

    }
}
