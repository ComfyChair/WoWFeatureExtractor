package org.jenhan.wowfeatureextractiontool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {
    String testFilePath = "src/test/resources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);
    List<Session.SessionInfo> sessionInfoList;
    List<Session.SessionInfo> expectedInfoList;

    @BeforeEach
    void setUp() {
        sessionInfoList = Session.readSessionInfo(testFile);
        expectedInfoList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688494496000L));
        expectedInfoList.add(new Session.SessionInfo(0, 3, "Arvensis", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688544677000L));
        expectedInfoList.add(new Session.SessionInfo(0, 389, "Sugi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688494046000L));
        expectedInfoList.add(new Session.SessionInfo(0, 571, "Arvensis", "Sen'jin",
                startTime));
    }

    @Test
    void getSessionInfo() {
        Session newSession = new Session(sessionInfoList.get(0));
        assertEquals(expectedInfoList.get(0), newSession.getSessionInfo());
    }

    @Test
    void readSessionInfo1() {
        assertEquals(3, sessionInfoList.size());
        // first session
        assertEquals(expectedInfoList.get(0), sessionInfoList.get(0));
        // second session
        assertEquals(expectedInfoList.get(1), sessionInfoList.get(1));
        // third session
        assertEquals(expectedInfoList.get(2), sessionInfoList.get(2));
    }

    @Test
    void exportToXML_TestStartOfDocument() throws IOException {
        File testOutput = new File("src/test/testOutput/exportTest2.xml");
        Session session = new Session(sessionInfoList.get(1));
        session.exportToXML(testFile, testOutput);
        // check header
        BufferedReader reader = new BufferedReader(new FileReader(testOutput));
        String headerLine = reader.readLine();
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>", headerLine);
        String collectionLine = reader.readLine();
        assertEquals("<gmaf-collection>", collectionLine);
        String emptyLine = reader.readLine();
        assertEquals("", emptyLine);
        String dataLine = reader.readLine();
        assertEquals("<gmaf-data>", dataLine);
        String fileLine = reader.readLine();
        assertEquals("<file>" + testFile.getName() + "</file>", fileLine);
        String dateLine = reader.readLine();
        assertTrue(isSimpleTag(dateLine, "date"));
        // check content
        String interactionLine = reader.readLine();
        assertTrue(isOpenTag(interactionLine, "interaction"));
    }

    private boolean isSimpleTag(String line, String tagString) {
        return line.startsWith("<" + tagString + ">") && line.endsWith("</" + tagString + ">");
    }

    private boolean isOpenTag(String line, String tagString) {
        return line.startsWith("<" + tagString) && line.endsWith(">");
    }

    private boolean isCloseTag(String line, String tagString) {
        return line.equals("</" + tagString + ">");
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
}