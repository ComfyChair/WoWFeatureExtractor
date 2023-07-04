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
    String testFilePath = "testRessources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);
    List<Session.SessionInfo> sessionInfoList;
    List<Session.SessionInfo> expectedInfoList;

    @BeforeEach
    void setUp() {
        sessionInfoList = Session.readSessionInfo(testFile);
        expectedInfoList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372354000L));
        expectedInfoList.add(new Session.SessionInfo(0, 3, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688389927000L));
        expectedInfoList.add(new Session.SessionInfo(0, 74, "Arvensis", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372378000L));
        expectedInfoList.add(new Session.SessionInfo(0, 89, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688370427000L));
        expectedInfoList.add(new Session.SessionInfo(0, 96, "Nepi", "Sen'jin",
                startTime));
    }

    @Test
    void getSessionInfo() {
        Session newSession = new Session(sessionInfoList.get(0));
        assertEquals(expectedInfoList.get(0), newSession.getSessionInfo());
    }

    @Test
    void readSessionInfo() {
        assertEquals(4, sessionInfoList.size());
        // first session
        assertEquals(expectedInfoList.get(0), sessionInfoList.get(0));
        // second session
        assertEquals(expectedInfoList.get(1), sessionInfoList.get(1));
        // third session
        assertEquals(expectedInfoList.get(2), sessionInfoList.get(2));
        // fourth session
        assertEquals(expectedInfoList.get(3), sessionInfoList.get(3));
    }


    @Test
    void exportToXML() throws IOException {
        File testOutput = new File("testOutput/exportTest1");
        Session session = new Session(sessionInfoList.get(0));
        session.exportToXML(testFile, testOutput);
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
        assertEquals("<file>" + testOutput.getName() + "</file>", fileLine);
    }

    @Test
    void timeConversionTest(){
        Calendar testTime = Calendar.getInstance();
        long unixTime = 1688372354L * 1000;
        testTime.setTime(new Date(unixTime));
        System.out.println("Tag: " + testTime.get(Calendar.DATE) + "."+
                (testTime.get(Calendar.MONTH)+1) + "." + testTime.get(Calendar.YEAR));
        int date = testTime.get(Calendar.DATE);
        assertEquals(3, date);
        int month = testTime.get(Calendar.MONTH);
        assertEquals(6, month); // JAN = 0
    }
}