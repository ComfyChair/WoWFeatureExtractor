package org.jenhan.wowfeatureextractor;

import javafx.scene.control.Alert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MainControlTest {
    MainControl testInstance;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final static String TEST_MSG = "test message";
    private final static String TEST_EXC_MSG = "test exception";
    private final static String TEST_TITLE = "test title";

    @BeforeEach
    void setUp() {
        testInstance = new MainControl();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void confirmationDialog() {
        // only test non-gui result here
        assertTrue(MainControl.confirmationDialog("foo"));
    }

    @Test
    void handleError() {
        MainControl.handleError(TEST_MSG, new Exception(TEST_EXC_MSG));
        String[] outputSplit = errContent.toString().split("\n");
        String expected_3 = "java.lang.Exception: " + TEST_EXC_MSG;

        assertEquals(TEST_MSG, outputSplit[0]);
        assertEquals(TEST_EXC_MSG, outputSplit[1]);
        assertEquals(expected_3, outputSplit[2]);
    }

    @Test
    void handleUserfeedback() {
        Alert.AlertType alertType = Alert.AlertType.ERROR;
        String expected = alertType + " - " + TEST_TITLE + ": " + TEST_MSG + "\n";

        MainControl.handleUserFeedback(alertType, TEST_MSG, TEST_TITLE);
        assertEquals(expected, outContent.toString());
        outContent.reset();

        alertType = Alert.AlertType.INFORMATION;
        expected = alertType + " - " + TEST_TITLE + ": " + TEST_MSG  + "\n";
        MainControl.handleUserFeedback(alertType, TEST_MSG, TEST_TITLE);
        assertEquals(expected, outContent.toString());
        outContent.reset();

        alertType = Alert.AlertType.WARNING;
        expected = alertType + " - " + TEST_TITLE + ": " + TEST_MSG + "\n";
        MainControl.handleUserFeedback(alertType, TEST_MSG, TEST_TITLE);
        assertEquals(expected, outContent.toString());
        outContent.reset();
    }

    @Test
    void getSessionInfos() {
        // only general retrieval
        List<Session> sessionList = MainControl.sessionList();
        assertTrue(sessionList.isEmpty());
    }

}