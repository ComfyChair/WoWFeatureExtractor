package org.jenhan.wowfeatureextractiontool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.loadui.testfx.GuiTest;

import java.io.IOException;

class MainControlGuiTest extends GuiTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void confirmationDialog() {
    }

    @Test
    void installAddon() {
    }

    @Test
    void selectFile() {
    }

    @Test
    void exportToXML() {
    }

    @Test
    void handleError() {
    }

    @Test
    void handleUserfeedback() {
    }

    @Test
    void sessionList() {
    }

    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return parent;
    }
}