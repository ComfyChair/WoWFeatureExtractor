package org.jenhan.wowfeatureextractor;

import javafx.application.Application;
import javafx.beans.value.ObservableIntegerValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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

    /** entry point **/
    public static void main(String[] args) {
        launch();
    }

    /** starts the graphical user interface **/
    @Override
    public void start(Stage primaryStage) throws IOException {
        Gui.primaryStage = primaryStage;
        isActive = true;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 240);
        primaryStage.setTitle("WoW Feature Extractor");
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
    static File promptForAddonInstallDir(String prefFolderString) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (prefFolderString != null) {
            File prefFolder = new File(prefFolderString);
            if (prefFolder.exists()){ // might have been deleted or renamed in the meantime
                directoryChooser.setInitialDirectory(prefFolder);
            }
        }
        directoryChooser.setTitle("Select installation directory");
        return directoryChooser.showDialog(getPrimaryStage());
    }

    /** opens a standard FileChooser dialog to select the output path and file name **/
    static File showSaveDialog(String prefFolderString) {
        FileChooser fileChooser = new FileChooser();
        if (prefFolderString != null) {
            File prefFolder = new File(prefFolderString);
            if (prefFolder.exists()){ // might have been deleted or renamed in the meantime
                fileChooser.setInitialDirectory(prefFolder);
            }
        }
        fileChooser.setTitle("Save output to:");
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
            File parentToPref = new File(prefFile).getParentFile();
            if (parentToPref.exists()){ // might have been deleted or renamed in the meantime
                fileChooser.setInitialDirectory(parentToPref);
            }
        }
        return fileChooser.showOpenDialog(getPrimaryStage());
    }

    /** opens a standard dialog with custom content to select a recorded session
     * dialog content created from session-selection-view.fxml
     * @return  a list of the selected session ids, empty List if canceled **/
    static List<Integer> promptForSession() {
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

    /** indicates whether the gui is active in order to reroute gui calls for testing purposes **/
    public static boolean isActive() {
        return isActive;
    }

}
