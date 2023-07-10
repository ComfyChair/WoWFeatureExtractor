package org.jenhan.wowfeatureextractiontool;

import org.jenhan.wowfeatureextractiontool.Utilities.DateFormatted;
import org.jenhan.wowfeatureextractiontool.Utilities.TimeFormatted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
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
        assertEquals(4, sessionList.size());
        // first session
        assertEquals(expectedList.get(0), sessionList.get(0));
        // second session
        assertEquals(expectedList.get(1), sessionList.get(1));
        // third session
        assertEquals(expectedList.get(2), sessionList.get(2));
        // fourth session
        assertEquals(expectedList.get(2), sessionList.get(2));
    }

    @Test
    void exportToXML_TestStartOfDocument() throws IOException {
        setShortTestExpected();
        File testOutput = new File("src/test/testOutput/exportTest2.xml");
        Session session = sessionList.get(1);
        System.out.println("Testing start of document: " + testOutput.getPath());
        System.out.println("Session Info: " + sessionList.get(1));
        session.exportToXML(testOutput);
        // check header
        BufferedReader reader = new BufferedReader(new FileReader(testOutput));
        String headerLine = reader.readLine().trim();
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>", headerLine.toLowerCase());
        String collectionLine = reader.readLine().trim();
        assertEquals("<gmaf-collection>", collectionLine);
        String dataLine = reader.readLine().trim();
        assertEquals("<gmaf-data>", dataLine);
        String fileLine = reader.readLine().trim();
        assertEquals("<file>" + testOutput.getName() + "</file>", fileLine);
        String dateLine = reader.readLine().trim();
        assertTrue(isSimpleTag(dateLine, "date"));
    }

    private boolean isSimpleTag(String line, String tagString) {
        return line.startsWith("<" + tagString + ">") && line.endsWith("</" + tagString + ">");
    }

    @Test
    void timeConversionTest(){
        Calendar testTime = Calendar.getInstance();
        long unixTime = 1688372354L * 1000;
        testTime.setTime(new Date(unixTime));
        int date = testTime.get(Calendar.DATE);
        assertEquals(3, date);
        int month = testTime.get(Calendar.MONTH);
        assertEquals(6, month); // JAN = 0
    }

    // convenience method for
    void setContentProperties(Session session, String charName, String serverName, Calendar calendar){
        session.charNameProperty().setValue(charName);
        session.serverNameProperty().setValue(serverName);
        session.dateProperty().setValue(new DateFormatted(calendar.getTime()));
        session.startTimeProperty().setValue(new TimeFormatted(calendar.getTime()));
    }
    void setShortTestExpected(){
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688840368000L));
        Session expectedSession1 = new Session(0);
        setContentProperties(expectedSession1,"Antigone", "Sen'jin", startTime);
        expectedList.add(expectedSession1);
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688891052000L));
        Session expectedSession2 = new Session(1);
        setContentProperties(expectedSession2, "Spice", "Sen'jin", startTime);
        expectedList.add(expectedSession2);
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688840385000L));
        Session expectedSession3 = new Session(2);
        setContentProperties(expectedSession3, "Antigone", "Sen'jin", startTime);
        expectedList.add(expectedSession3);
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688840200000L));
        Session expectedSession4 = new Session(2);
        setContentProperties(expectedSession3, "Antigone", "Sen'jin", startTime);
        expectedList.add(expectedSession4);
    }
}