package org.jenhan.wowfeatureextractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {
    String testFilePath = "src/test/resources/ShortSessionsTest.lua";
    File testFile = new File(testFilePath);
    List<Session> sessionList;
    List<Session> expectedList;

    @BeforeEach
    void setUp() {
        SessionManager manager = SessionManager.getInstance();
        sessionList = manager.getSessionList(testFile);
        expectedList = new ArrayList<>();
    }

    @Test
    void readSessionInfo1() {
        setShortTestExpected();
        int expectedSessions = 3;
        assertEquals(expectedSessions, sessionList.size());
        for (int i = 0; i < expectedSessions; i++) {
            assertEquals(expectedList.get(i), sessionList.get(i));
        }
    }

    @Test
    void exportToXML_TestStartOfDocument() throws IOException {
        setShortTestExpected();
        File testOutput = new File("src/test/testOutput/exportTest2.xml");
        if (!testOutput.getParentFile().exists()){
            testOutput.getParentFile().mkdirs();
        }
        Session session = sessionList.get(1);
        System.out.println("Testing start of document: " + testOutput.getPath());
        System.out.println("Session Info: " + sessionList.get(1));
        session.exportToXML(testOutput);
        // check header
        BufferedReader reader = new BufferedReader(new FileReader(testOutput));
        String headerLine = reader.readLine().trim();
        assertEquals(LuaToXML.DECLARATION, headerLine.toLowerCase());
        String collectionLine = reader.readLine().trim();
        assertEquals("<gmaf-collection>", collectionLine);
        String dataLine = reader.readLine().trim();
        assertEquals("<gmaf-data>", dataLine);
        String fileLine = reader.readLine().trim();
        assertEquals("<file>" + testFile.getName() + "</file>", fileLine);
        String dateLine = reader.readLine().trim();
        assertTrue(isSimpleTag(dateLine, "date"));
    }

    private boolean isSimpleTag(String line, String tagString) {
        return line.startsWith("<" + tagString + ">") && line.endsWith("</" + tagString + ">");
    }

    @Test
    void timeConversionTest(){
        Calendar testTime = Calendar.getInstance();
        long unixTime = 1688840368L * 1000;
        testTime.setTime(new Date(unixTime));
        int date = testTime.get(Calendar.DATE);
        assertEquals(8, date);
        int month = testTime.get(Calendar.MONTH);
        assertEquals(6, month); // JAN = 0
        int hour = testTime.get(Calendar.HOUR);
        assertEquals(8, hour);
        int minute = testTime.get(Calendar.MINUTE);
        assertEquals(19, minute);
    }

    // convenience method for
    void setContentProperties(Session session, String charName, String serverName, Date date){
        session.charNameProperty().setValue(charName);
        session.serverNameProperty().setValue(serverName);
        session.setDateTime(date);
    }
    void setShortTestExpected(){
        long unixTime = 1689580415L * 1000L;
        Session expectedSession1 = Session.create(0, testFile.getName());
        setContentProperties(expectedSession1,"Ángua", "TestServer", new Date(unixTime));
        expectedList.add(expectedSession1);

        unixTime = 1689580493L * 1000L;
        Session expectedSession2 = Session.create(1, testFile.getName());
        setContentProperties(expectedSession2, "Spice", "Sen'jin", new Date(unixTime));
        expectedList.add(expectedSession2);

        unixTime = 1689580359L * 1000L;
        Session expectedSession3 = Session.create(2, testFile.getName());
        setContentProperties(expectedSession3, "Arvensis", "Sen'jin", new Date(unixTime));
        expectedList.add(expectedSession3);
    }

    @Test
    void testHashCode() {
        setShortTestExpected();
        assertNotEquals(sessionList.get(0).hashCode(), sessionList.get(1).hashCode());
        assertEquals(sessionList.get(0).hashCode(), expectedList.get(0).hashCode());
    }
}