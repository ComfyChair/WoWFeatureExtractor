package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;

public class Gui extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch();
    }



    @Override
    public void start(Stage primaryStage) throws IOException {
        Gui.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 240);
        primaryStage.setTitle("WoW Feature Extraction Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static public Stage getPrimaryStage() {
        return Gui.primaryStage;
    }

    static File promptForFolder(String message, String prefFolder) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (prefFolder != null){
            directoryChooser.setInitialDirectory(new File(prefFolder));
        }
        directoryChooser.setTitle(message);
        return  directoryChooser.showDialog(getPrimaryStage());
    }

    public static File promptForFile(String message) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        return  fileChooser.showOpenDialog(getPrimaryStage());
    }

    public static void promptForSession() {
        //TODO: prompt for session
    }

    //TODO: create dialogs instead of console messages
    public static void errorMessage(String message) {
        System.out.println(message);
    }
    public static boolean confirm(String message) {
        System.out.println(message);
        return true;
    }
}
