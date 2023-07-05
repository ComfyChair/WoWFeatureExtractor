package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        File selectedDir = showDirectoryChooser(primaryStage);
        if (selectedDir != null) {
            System.out.println("Selected dir: " + selectedDir);
            mainControl.installAddon(selectedDir);
        }
    }

    private File showDirectoryChooser(Window window) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select installation directory");
        return  directoryChooser.showDialog(new Stage(StageStyle.UNIFIED));
    }

    public void onSelectFolderClick(ActionEvent actionEvent) {
        File selectedDir = showDirectoryChooser(primaryStage);
        if (selectedDir != null) {
            mainControl.selectSavedVarFolder(selectedDir);
        }
    }

    public void onExportToXmlClick(ActionEvent actionEvent) {

    }


}
