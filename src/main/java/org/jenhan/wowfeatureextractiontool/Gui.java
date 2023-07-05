package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;

public class Gui extends Application {
    MainControl mainControl = MainControl.getInstance();
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch();
    }




    @Override
    public void start(Stage primaryStage) throws IOException {
        setPrimaryStage(primaryStage);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 120);
        primaryStage.setTitle("WoW Feature Extraction Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setPrimaryStage(Stage stage) {
        Gui.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Gui.primaryStage;
    }

    public void onInstallAddonClick(ActionEvent actionEvent) {
        File selectedDir = promptForFolder("Select installation directory");
        if (selectedDir != null) {
            System.out.println("Selected dir: " + selectedDir);
            mainControl.installAddon(selectedDir);
        }
    }

    public void onSelectFolderClick(ActionEvent actionEvent) {
        File selectedDir = promptForFolder("Select SavedVariables directory");
        if (selectedDir != null) {
            mainControl.selectSavedVarFile(selectedDir);
        }
    }

    public void onExportToXmlClick(ActionEvent actionEvent) {
        File selectedDir = promptForFolder("Select export directory");
        if (selectedDir != null) {
            mainControl.exportToXML(selectedDir);
        }
    }

    static File promptForFolder(String message) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
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

    public static void errorMessage(String message) {
        System.out.println(message);
    }
}
