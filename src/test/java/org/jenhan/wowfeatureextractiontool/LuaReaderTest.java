package org.jenhan.wowfeatureextractiontool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LuaReaderTest {
    File shortSessionsFile = new File("src/test/resources/ShortSessionsTest.lua");
    LuaReader testReader;
    List<Session> expectedSessionList;
    List<SessionData> sessionData;
    SessionData session_0 = new SessionData("Antigone", "TestServer", new Date(1688840368L *1000));
    SessionData session_1 = new SessionData("Spice", "Sen'jin", new Date(1688891052L * 1000));
    SessionData session_2 = new SessionData("Antigone","Sen'jin", new Date(1688840385L * 1000));
    SessionData session_3 = new SessionData("Antigone", "Sen'jin", new Date(1688840200L * 1000));

    @BeforeEach
    void setUp() {
        testReader = new LuaReader();
        expectedSessionList = new ArrayList<>();
        List<SessionData> sessionData = new ArrayList<>();
        Collections.addAll(sessionData, session_0, session_1, session_2, session_3);
        for (int i = 0; i < 4; i++) {
            Session newSession = Session.create(i, shortSessionsFile.getName());
            expectedSessionList.add(newSession);
            SessionData thisSessionData = sessionData.get(i);
            newSession.setCharName(thisSessionData.charName);
            newSession.setDateTime(thisSessionData.date);
            newSession.setServerName(thisSessionData.serverName);
        }

    }

    @Test
    void readFile() {
        List<Session> actualList = testReader.readFile(shortSessionsFile);
        assertEquals(expectedSessionList.size(), actualList.size());
        for (int i = 0; i < actualList.size(); i++) {
            Session actual = actualList.get(i);
            Session expected = expectedSessionList.get(i);
            assertEquals(expected.sessionIDProperty().get(), actual.sessionIDProperty().get());
            assertEquals(expected.getFileName(), actual.getFileName());
            assertEquals(expected.charNameProperty().get(), actual.charNameProperty().get());
            assertEquals(expected.serverNameProperty().get(), actual.serverNameProperty().get());
            assertEquals(expected.dateProperty().get().toString(), actual.dateProperty().get().toString());
            assertEquals(expected.startTimeProperty().get().toString(), actual.startTimeProperty().get().toString());
        }

    }

    record SessionData(String charName, String serverName, Date date){}

}