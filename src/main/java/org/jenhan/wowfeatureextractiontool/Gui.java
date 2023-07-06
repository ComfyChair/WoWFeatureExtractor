package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

    public static File promptForFile(String message, String prefFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        // only allow .lua files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lua Files", "*.lua"));
        // set previously chosen file as default (if there is any)
        if (prefFile != null){
            fileChooser.setInitialDirectory(new File(prefFile).getParentFile());
        }
        return  fileChooser.showOpenDialog(getPrimaryStage());
    }

    public static void promptForSession() {
        System.out.println("Session selection necessary");
        //TODO: prompt for session
    }

    //TODO: create dialogs instead of console messages
    public static void errorMessage(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Error");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
    public static boolean confirm(String message) {
        System.out.println(message);
        return true;
    }
    public static boolean success(String message) {
        System.out.println(message);
        return true;
    }
}
