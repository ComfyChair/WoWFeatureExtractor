package org.jenhan;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {
    String testFilePath = "/home/jenny/IdeaProjects/WoWFeatureExtractionTool/testRessources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);
    List<Session.SessionInfo> sessionInfoList;
    List<Session.SessionInfo> expectedInfoList;

    @BeforeEach
    void setUp() {
        sessionInfoList = Session.readSessionInfo(testFile);
        expectedInfoList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372354));
        expectedInfoList.add(new Session.SessionInfo(0, 3, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688389927));
        expectedInfoList.add(new Session.SessionInfo(0, 74, "Arvensis", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688372378));
        expectedInfoList.add(new Session.SessionInfo(0, 89, "Nepi", "Sen'jin",
                startTime));
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688370427));
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


}