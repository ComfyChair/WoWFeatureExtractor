package org.jenhan.wowfeatureextractiontool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //TODO: start GUI
        FXMLLoader fxmlLoader = new FXMLLoader(org.jenhan.wowfeatureextractiontool.Main.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 120);
        stage.setTitle("WoW Feature Extraction Tool");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}


