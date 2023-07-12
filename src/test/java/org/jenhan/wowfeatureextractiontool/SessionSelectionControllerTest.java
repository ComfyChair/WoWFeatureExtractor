package org.jenhan.wowfeatureextractiontool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

class SessionSelectionControllerTest {
    SessionSelectionController testController;
    File shortSessionsFile = new File("src/test/resources/ShortSessionsTest.lua");

    @BeforeEach
    void setUp() {
        testController = new SessionSelectionController();
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.getSessionList(shortSessionsFile);

    }

    @Test
    void populateTable() {
    }

    @Test
    void getSelected() {
    }
}