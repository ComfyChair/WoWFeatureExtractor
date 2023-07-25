package org.jenhan.wowfeatureextractor;

import javafx.scene.Scene;
import javafx.scene.control.Button;
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

import java.io.File;
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
    private final File testInput = new File("src/test/resources/ShortSessionsTest.lua");
    private int stepNo;

    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private void start(Stage stage) throws IOException {
        gui.start(stage);
        scene = stage.getScene();
    }

    @AfterEach
    void releaseEvents() throws TimeoutException {
        FxToolkit.cleanupApplication(gui);
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
        verifyThat(installBtn, LabeledMatchers.hasText("Install AddOn"));
        verifyThat(selectBtn, LabeledMatchers.hasText("Select Input"));
        verifyThat(exportBtn, LabeledMatchers.hasText("Export XML"));
    }

    /**
     * tests if file chooser dialog is opening on button click
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void installAddonBtn(FxRobot robot) {
        step("Install Addon", () -> {
            robot.clickOn(INSTALL_BTN);
            assertFalse(scene.lookup(MAIN_ROOT).isFocused()); // main view loses focus
        });
        // cannot test further, as the standard  FileChooser and DirectoryChooser work with system windows,
        // which are not accessible in this version of testFx
    }

    /**
     * tests if file chooser dialog is opening on button click
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void selectInputBtn(FxRobot robot) {
        step("Select File", () -> {
            robot.clickOn(SELECT_BTN);
            assertFalse(scene.lookup(MAIN_ROOT).isFocused()); // main view loses focus
        });
        // cannot test further, as the standard  FileChooser and DirectoryChooser work with system windows,
        // which are not accessible in this version of testFx
    }

    /**
     * tests if file chooser dialog is opening on button click
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void exportToXMLBtn(FxRobot robot) {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.getSessionList(testInput);
        robot.clickOn(EXPORT_BTN);
        assertFalse(scene.lookup(MAIN_ROOT).isFocused()); // main view loses focus
        // cannot test complete walkthrough, as the standard  FileChooser and DirectoryChooser
        // work with system windows, which are inaccessible in this version of testFx
    }

    private void step(final String step, final Runnable runnable) {
        ++stepNo;
        LOG.info(String.format("STEP %d: Begin - %s", stepNo, step));
        runnable.run();
        LOG.info(String.format("STEP %d: End - %s", stepNo, step));
    }
}