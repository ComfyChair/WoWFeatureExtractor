package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.beans.value.ObservableIntegerValue;
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
    // returns a list of the selected session ids
    public static List<Integer> promptForSession() {
        // create dialog with content from fxml
        FXMLLoader loader = new FXMLLoader(SessionSelectionController.class.getResource("session-selection-view.fxml"));
        Dialog<ButtonType> sessionSelectionDialog = new Dialog<>();
        sessionSelectionDialog.initOwner(primaryStage);
        sessionSelectionDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            errorMessage("Error in session selection dialog creation");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        // populate session table in dialog content
        SessionSelectionController controller = loader.getController();
        controller.populateTable();
        sessionSelectionDialog.getDialogPane().setContent(dialogContent);
        List<Integer> result = new ArrayList<>(); // default for: failure or no selection
        // show dialog and wait for results
        Optional<ButtonType> response = sessionSelectionDialog.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            result = controller.getSelected().stream()
                    .map(SessionInfo::sessionIDProperty)
                    .map(ObservableIntegerValue::get)
                    .toList();
        }
        System.out.println("selected " + result.size() + " sessions with id: " + Arrays.toString(result.toArray()));
        // return session id or -1 when canceled
        return result;
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
