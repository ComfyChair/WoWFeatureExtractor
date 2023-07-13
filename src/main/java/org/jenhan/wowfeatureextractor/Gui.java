package org.jenhan.wowfeatureextractor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableIntegerValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Main GUI controller class **/
public class Gui extends Application {
    private static Stage primaryStage;
    private static boolean isActive;

    /** entry point
     * @param args standard arguments, not considered **/
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(Gui::showError);
        launch();
    }

    /** starts the graphical user interface **/
    @Override
    public void start(Stage primaryStage) throws IOException {
        Gui.primaryStage = primaryStage;
        isActive = true;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 240);
        primaryStage.setTitle("WoW Feature Extraction Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        isActive = false;
        super.stop();
    }

    /** lets other classes access the primary stage  for error messages **/
    static Stage getPrimaryStage() {
        return Gui.primaryStage;
    }

    /** opens a standard FileChooser dialog to select a folder for addon installation **/
    static File promptForFolder(String message, String prefFolder) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (prefFolder != null) {
            directoryChooser.setInitialDirectory(new File(prefFolder));
        }
        directoryChooser.setTitle(message);
        return directoryChooser.showDialog(getPrimaryStage());
    }

    static File showSaveDialog(String message, String prefFolder) {
        FileChooser fileChooser = new FileChooser();
        if (prefFolder != null) {
            fileChooser.setInitialDirectory(new File(prefFolder));
        }
        fileChooser.setTitle(message);
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selected = fileChooser.showSaveDialog(getPrimaryStage());
        if (selected != null && !selected.getName().endsWith(".xml")){
            String extendedFileName = selected.getName() + ".xml";
            selected = selected.getParentFile()
                    .toPath().resolve(new File(extendedFileName).toPath()).toFile();
        }
        return selected;
    }

    /** opens a standard FileChooser dialog to select a lua file as input **/
    static File promptForFile(String message, String prefFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Lua Files", "*.lua"));
        if (prefFile != null) {
            fileChooser.setInitialDirectory(new File(prefFile).getParentFile());
        }
        return fileChooser.showOpenDialog(getPrimaryStage());
    }

    /** opens a standard dialog with custom content to select a recorded session
     * dialog content created from session-selection-view.fxml
     * @return  a list of the selected session ids, empty List if canceled **/
    static List<Integer> promptForSession() {
        FXMLLoader loader = new FXMLLoader(SessionSelectionController.class.getResource("/views/session-selection-view.fxml"));
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
     * @return List of selected session ids **/
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

    //TODO: Deutsche Lokalisierung hinzufügen für GUI?
    /** reroutes uncaught exceptions to Gui, if loaded **/
    private static void showError(Thread thread, Throwable e) {
        System.err.println("*** Default exception handler ***");
        if (Platform.isFxApplicationThread()) {
            System.out.println("Gui is running, rerouting error message");
            showErrorDialog(e);
            e.printStackTrace();
        } else {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /** shows exception dialog and prints stacktrace for uncaught exceptions **/
    private static void showErrorDialog(Throwable e) {
        feedbackDialog(Alert.AlertType.ERROR, e.getMessage(), "Uncaught Exception");
        e.printStackTrace();
    }

    /** shows user feedback dialog **/
    static void feedbackDialog(Alert.AlertType type, String message, String title){
        Alert dialog = new Alert(type);
        if (!title.isEmpty()){
            dialog.setTitle(title);
        }
        dialog.setContentText(message);
        dialog.showAndWait();
    }

    /** shows user confirmation dialog
     * @return true if user confirmed **/
    static boolean confirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean isActive() {
        return isActive;
    }

}
