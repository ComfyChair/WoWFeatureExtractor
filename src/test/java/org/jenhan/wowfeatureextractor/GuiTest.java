package org.jenhan.wowfeatureextractor;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(ApplicationExtension.class)
class GuiTest {
    private static final Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Gui gui = new Gui();
    private Scene scene;
    private final static String MAIN_ROOT ="#mainViewRoot";
    private final static String INSTALL_BTN ="#installBtn";
    private final static String SELECT_BTN = "#selectBtn";
    private final static String EXPORT_BTN = "#exportBtn";
    int stepNo;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) throws IOException, TimeoutException {
        gui.start(stage);
        scene = stage.getScene();
    }

    @AfterEach
    void releaseEvents() throws TimeoutException {
        FxToolkit.cleanupApplication(gui);
        // TODO: release events

    }

    @AfterAll
    static void cleanUp() throws Exception {
        FxToolkit.cleanupStages();
    }


    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void testButtonText(FxRobot robot) {
        Button installBtn = (Button) scene.lookup(INSTALL_BTN);
        Button selectBtn = (Button) scene.lookup(SELECT_BTN);
        Button exportBtn = (Button) scene.lookup(EXPORT_BTN);
        verifyThat(installBtn, LabeledMatchers.hasText("Install addon"));
        verifyThat(selectBtn, LabeledMatchers.hasText("Select input"));
        verifyThat(exportBtn, LabeledMatchers.hasText("Export XML"));
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void installAddon(FxRobot robot) {
        step("Install Addon", () -> {
            robot.clickOn(INSTALL_BTN);
            assertFalse(scene.lookup(MAIN_ROOT).isFocused());
            robot.clickOn(INSTALL_BTN);
            Node pane = scene.lookup(DirectoryChooser.class.descriptorString());
            //TODO: complete test
        });
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void selectInput(FxRobot robot) {
        //TODO: walkthrough selectInput
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void exportToXML(FxRobot robot) {
        //TODO: walkthrough exportToXML
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void confirmationDialog(FxRobot robot) {
        //TODO: write test
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void handleError(FxRobot robot) {
        //TODO: write test
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void handleUserfeedback(FxRobot robot) {
        //TODO: write test
    }


    private void step(final String step, final Runnable runnable) {
        ++stepNo;
        LOG.info(String.format("STEP %d: Begin - %s", stepNo, step));
        runnable.run();
        LOG.info(String.format("STEP %d: End - %s", stepNo, step));
    }


}