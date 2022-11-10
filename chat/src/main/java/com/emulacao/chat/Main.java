package com.emulacao.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.fazecast.jSerialComm.*;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("chatir.ico")));
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("novo.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChatIR v0.7");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}

