package com.emulacao.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainV2 extends Application
{
    static Scene scene;
    @Override
    public void start(Stage stage) throws Exception
    {
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("chatir.ico")));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("novoV2.fxml"));
        scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChatIR 2-jan-2023");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}

