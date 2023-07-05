package org.jenhan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {
    String testFilePath_1 = "testRessources/TestSession.lua";
    File testFile_1 = new File(testFilePath_1);
    List<Session.SessionInfo> sessionInfoList_1;
    List<Session.SessionInfo> expectedInfoList_1;

    String testFilePath_2 = "testRessources/FeatureRecordingTool.lua";
    File testFile_2 = new File(testFilePath_2);
    List<Session.SessionInfo> sessionInfoList_2;
    List<Session.SessionInfo> expectedInfoList_2;

    @BeforeEach
    void setUp() {
        sessionInfoList_1 = Session.readSessionInfo(testFile_1);
        sessionInfoList_2 = Session.readSessionInfo(testFile_2);
        expectedInfoList_1 = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372354000L));
        expectedInfoList_1.add(new Session.SessionInfo(0, 3, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688389927000L));
        expectedInfoList_1.add(new Session.SessionInfo(0, 74, "Arvensis", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372378000L));
        expectedInfoList_1.add(new Session.SessionInfo(0, 89, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688370427000L));
        expectedInfoList_1.add(new Session.SessionInfo(0, 96, "Nepi", "Sen'jin",
                startTime));

        expectedInfoList_2 = new ArrayList<>();
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372354000L));
        expectedInfoList_2.add(new Session.SessionInfo(0, 3, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688389927000L));
        expectedInfoList_2.add(new Session.SessionInfo(0, 74, "Arvensis", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372378000L));
        expectedInfoList_2.add(new Session.SessionInfo(0, 89, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688370427000L));
        expectedInfoList_2.add(new Session.SessionInfo(0, 96, "Nepi", "Sen'jin",
                startTime));
    }

    @Test
    void getSessionInfo() {
        Session newSession = new Session(sessionInfoList_1.get(0));
        assertEquals(expectedInfoList_1.get(0), newSession.getSessionInfo());
    }

    @Test
    void readSessionInfo1() {
        assertEquals(4, sessionInfoList_1.size());
        // first session
        assertEquals(expectedInfoList_1.get(0), sessionInfoList_1.get(0));
        // second session
        assertEquals(expectedInfoList_1.get(1), sessionInfoList_1.get(1));
        // third session
        assertEquals(expectedInfoList_1.get(2), sessionInfoList_1.get(2));
        // fourth session
        assertEquals(expectedInfoList_1.get(3), sessionInfoList_1.get(3));
    }


    @Test
    void exportToXML_TestHeader() throws IOException {
        File testOutput = new File("testOutput/exportTest1.xml");
        Session session = new Session(sessionInfoList_1.get(0));
        session.exportToXML(testFile_1, testOutput);
        // check output
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
        assertEquals("<file>" + testFile_1.getName() + "</file>", fileLine);
        String dateLine = reader.readLine();
        assertTrue(isSimpleTag(dateLine, "date"));
    }

    @Test
    void exportToXML_TestContent() throws IOException {
        File testOutput = new File("testOutput/exportTest2.xml");
        Session session = new Session(sessionInfoList_2.get(0));
        session.exportToXML(testFile_2, testOutput);
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
        assertEquals("<file>" + testFile_2.getName() + "</file>", fileLine);
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