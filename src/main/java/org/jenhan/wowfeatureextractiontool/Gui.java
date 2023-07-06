package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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
        if (prefFolder != null) {
            directoryChooser.setInitialDirectory(new File(prefFolder));
        }
        directoryChooser.setTitle(message);
        return directoryChooser.showDialog(getPrimaryStage());
    }

    public static File promptForFile(String message, String prefFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        // only allow .lua files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lua Files", "*.lua"));
        // set previously chosen file as default (if there is any)
        if (prefFile != null) {
            fileChooser.setInitialDirectory(new File(prefFile).getParentFile());
        }
        return fileChooser.showOpenDialog(getPrimaryStage());
    }

    // opens a dialog to select a recorded session
    // returns the session id or -1 in case of failure
    public static int promptForSession() {
        System.out.println("Session selection necessary");
        FXMLLoader dialogLoader = new FXMLLoader(Gui.class.getResource("select-session-view.fxml"));
        System.out.println("Loader created");
        Dialog<Integer> sessionSelectionDialog = new Dialog<>();
        try {
            sessionSelectionDialog.setDialogPane(new FXMLLoader(
                    Gui.class.getResource("select-session-view.fxml")).load());
            Optional<Integer> result = sessionSelectionDialog.showAndWait();
            if (result.isPresent()){
                return result.get();
            } else {
                return -1;
            }
        } catch (IOException e) {
            Gui.errorMessage("Error in session selection dialog");
            return -1;
        }
    }

    public static void errorMessage(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Error");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static boolean confirm(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmation needed");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    public static void notice(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Notice");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    public static void success(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Success");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
