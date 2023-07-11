package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableIntegerValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jenhan.wowfeatureextractiontool.Utilities.ErrorController;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Gui extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(Gui::showError);
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

    /** lets other classes access the primary stage  for error messages */
    static Stage getPrimaryStage() {
        return Gui.primaryStage;
    }

    /** opens a standard FileChooser dialog to select a folder for addon installation */
    static File promptForFolder(String message, String prefFolder) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (prefFolder != null) {
            directoryChooser.setInitialDirectory(new File(prefFolder));
        }
        directoryChooser.setTitle(message);
        return directoryChooser.showDialog(getPrimaryStage());
    }

    /** opens a standard FileChooser dialog to select a lua file as input */
    public static File promptForFile(String message, String prefFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lua Files", "*.lua"));
        if (prefFile != null) {
            fileChooser.setInitialDirectory(new File(prefFile).getParentFile());
        }
        return fileChooser.showOpenDialog(getPrimaryStage());
    }

    /** opens a standard dialog with custom content to select a recorded session
     * dialog content created from session-selection-view.fxml
     * @return  a list of the selected session ids, empty List if canceled */
    public static List<Integer> promptForSession() {
        FXMLLoader loader = new FXMLLoader(SessionSelectionController.class.getResource("session-selection-view.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Session selection");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Parent dialogContent;
        try {
            dialogContent = loader.load();
        } catch (IOException e) {
            feedbackDialog(Alert.AlertType.ERROR, "Error in session selection dialog creation", "IO Error");
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        return sessionSelection(loader, dialog, dialogContent);
    }

    /** handles populating session table in selection dialog and user response
     * @return List of selected session ids */
    private static List<Integer> sessionSelection(FXMLLoader loader, Dialog<ButtonType> dialog, Parent dialogContent) {
        SessionSelectionController controller = loader.getController();
        controller.populateTable();
        dialog.getDialogPane().setContent(dialogContent);
        List<Integer> result = new ArrayList<>(); // default for: failure or no selection
        Optional<ButtonType> response = dialog.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            result = controller.getSelected().stream()
                    .map(Session::sessionIDProperty)
                    .map(ObservableIntegerValue::get)
                    .toList();
        }
        return result;
    }

    /** reroutes uncaught exceptions to Gui, if loaded */
    private static void showError(Thread thread, Throwable e) {
        System.err.println("*** Default exception handler ***");
        if (Platform.isFxApplicationThread()) {
            showErrorDialog(e);
        } else {
            System.err.println(e.getMessage() + e);
        }
    }

    /** shows exception dialog */
    private static void showErrorDialog(Throwable e) {
        StringWriter errorMsg = new StringWriter();
        e.printStackTrace(new PrintWriter(errorMsg));
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("Error.fxml"));
        try {
            Parent root = loader.load();
            ((ErrorController) loader.getController()).setErrorText(errorMsg.toString());
            dialog.setScene(new Scene(root, 250, 400));
            dialog.show();
            e.printStackTrace(); // print in console nonetheless
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /** shows user feedback dialog */
    public static void feedbackDialog(Alert.AlertType type, String message, String title){
        Alert dialog = new Alert(type);
        dialog.setTitle(title);
        dialog.setContentText(message);
        dialog.showAndWait();
    }

    /** shows user confirmation dialog
     * @return true if user confirmed */
    public static boolean confirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
