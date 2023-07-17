package org.jenhan.wowfeatureextractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionManagerTest {

    private static SessionManager testManager;
    private final static  File shortSessionsFile = new File("src/test/resources/ShortSessionsTest.lua");
    private final static  File mediumLengthFile = new File("src/test/resources/MediumLengthSession.lua");
    private final static  List<Session> expectedList = new ArrayList<>();
    private XmlValidator validator;
    private final static File xsdFile = new File("src/test/resources/gmaf-interaction.xsd");

    @BeforeEach
    void setUp() throws SAXException {
        testManager = SessionManager.getInstance();
        validator = new XmlValidator(xsdFile);
        List<LuaReaderTest.SessionData> sessionData = new ArrayList<>();
        LuaReaderTest.SessionData session_0 = new LuaReaderTest.SessionData("Ángua", "TestServer", new Date(1689580415L *1000));
        LuaReaderTest.SessionData session_1 = new LuaReaderTest.SessionData("Spice", "Sen'jin", new Date(1689580493L * 1000));
        LuaReaderTest.SessionData session_2 = new LuaReaderTest.SessionData("Arvensis","Sen'jin", new Date(1689580359L * 1000));
        Collections.addAll(sessionData, session_0, session_1, session_2);
        for (int i = 0; i < 3; i++) {
            Session newSession = Session.create(i, shortSessionsFile.getName());
            expectedList.add(newSession);
            LuaReaderTest.SessionData thisSessionData = sessionData.get(i);
            newSession.setCharName(thisSessionData.charName());
            newSession.setDateTime(thisSessionData.date());
            newSession.setServerName(thisSessionData.serverName());
        }
    }

    @Test
    void getInstance() {
        SessionManager sm1 = SessionManager.getInstance();
        SessionManager sm2 = SessionManager.getInstance();
        assertEquals(testManager, sm1);
        assertEquals(testManager, sm2);
    }

    @Test
    void getSessionList() {
        List<Session> actualList = testManager.getSessionList(shortSessionsFile);
        for (int i = 0; i < 3; i++) {
            Session actual = actualList.get(i);
            Session expected = expectedList.get(i);
            assertEquals(expected.sessionIDProperty().get(), actual.sessionIDProperty().get());
            assertEquals(expected.getFileName(), actual.getFileName());
            assertEquals(expected.charNameProperty().get(), actual.charNameProperty().get());
            assertEquals(expected.serverNameProperty().get(), actual.serverNameProperty().get());
            assertEquals(expected.dateProperty().get().toString(), actual.dateProperty().get().toString());
            assertEquals(expected.startTimeProperty().get().toString(), actual.startTimeProperty().get().toString());
        }
    }

    @Test
    void exportToXML() throws IOException, SAXException {
        String outPath = "src/test/testOutput/test.xml";
        File outFile = new File(outPath);
        testManager.getSessionList(shortSessionsFile);
        // single session export
        List<File> outList = testManager.exportToXML(outFile, List.of(0));
        int exceptions = validator.validate(outList.get(0));
        assertEquals(0, exceptions);
        // multiple session export
        outList = testManager.exportToXML(outFile, Arrays.asList(1, 2));
        for (File file : outList
        ) {
            exceptions = validator.validate(file);
        }
        assertEquals(0, exceptions);
    }

    @Test
    void validateMediumLengthExport() throws IOException, SAXException {
        String outPath = "src/test/testOutput/mediumLength.xml";
        File outFile = new File(outPath);
        testManager.getSessionList(mediumLengthFile);
        // single session export
        List<File> outList = testManager.exportToXML(outFile, List.of(0));
        int exceptions = validator.validate(outList.get(0));
        assertEquals(0, exceptions);
    }





}