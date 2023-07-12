package org.jenhan.wowfeatureextractiontool;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
class GuiTest {
    private Gui gui = new Gui();
    private Scene scene;
    private Button installBtn, selectBtn, exportBtn;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) throws IOException, TimeoutException {
        gui.start(stage);

        scene = stage.getScene();
        installBtn = (Button) scene.lookup("#installBtn");
        selectBtn = (Button) scene.lookup("#selectBtn");
        exportBtn = (Button) scene.lookup("#exportBtn");
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void testButtonText(FxRobot robot) {
        FxAssert.verifyThat(installBtn, LabeledMatchers.hasText("Install addon"));
        FxAssert.verifyThat(selectBtn, LabeledMatchers.hasText("Select input"));
        FxAssert.verifyThat(exportBtn, LabeledMatchers.hasText("Export XML"));
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void installAddon(FxRobot robot) {

    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void selectFile(FxRobot robot) {
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void exportToXML(FxRobot robot) {
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void confirmationDialog(FxRobot robot) {

    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void handleError(FxRobot robot) {
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void handleUserfeedback(FxRobot robot) {
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void sessionList(FxRobot robot) {
    }

}