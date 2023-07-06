package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        Dialog<ButtonType> sessionSelectionDialog = getSessionSelectionDialog();
        if (sessionSelectionDialog != null){
            System.out.println("Session selection dialog created");
            int result = -1;
            // show dialog and wait for results
            System.out.println("Show dialog");
            Optional<ButtonType> response = sessionSelectionDialog.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.OK) {
                //TODO: get session from response
            }
            // return session id or -1 when canceled
            return result;
        } else {
            return -1;
        }
    }

    private static Dialog<ButtonType> getSessionSelectionDialog() {
        FXMLLoader loader = new FXMLLoader(SessionSelectionController.class.getResource("session-selection-view.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        try {
            Parent dialogContent = loader.load();
            System.out.println("Content loaded");
            SessionSelectionController controller = loader.getController();
            System.out.println("About to populate table");
            controller.populateTable();
            System.out.println("Setting content");
            dialog.getDialogPane().setContent(dialogContent);
        } catch (IOException e) {
            errorMessage("Error in session selection dialog creation");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return dialog;
    }


    // user feedback dialogs
    public static void errorMessage(String message) {
        Alert alertMessage = new Alert(Alert.AlertType.ERROR);
        alertMessage.setContentText(message);
        alertMessage.showAndWait();
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void notice(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void success(String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Success!");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
