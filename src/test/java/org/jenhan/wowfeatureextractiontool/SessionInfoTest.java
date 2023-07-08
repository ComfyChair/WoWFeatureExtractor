package org.jenhan.wowfeatureextractiontool;

import org.jenhan.wowfeatureextractiontool.Utilities.DateFormatted;
import org.jenhan.wowfeatureextractiontool.Utilities.TimeFormatted;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SessionInfoTest {
    int expectedID, expectedLine;
    String expectedCharName, expectedServer, expectedDate, expectedTime;
    Calendar testCalendar;
    SessionInfo validInfo;

    { // init test data for all
        testCalendar = Calendar.getInstance();
        Date testDate = new Date(1688494496000L);
        testCalendar.setTime(testDate);
        expectedDate = new DateFormatted(testDate).toString();
        expectedTime = new TimeFormatted(testDate).toString();
        expectedID = 0;
        expectedLine = 1;
        expectedCharName = "TestChar";
        expectedServer = "TestServer";
        validInfo = new SessionInfo(expectedID, expectedLine);
        validInfo.setContentProperties(expectedCharName, expectedServer, testCalendar, 0);
    }

    @Test
    void sessionIDProperty() {
        assertEquals(expectedID, validInfo.sessionIDProperty().get());
    }

    @Test
    void charNameProperty() {
        assertEquals(expectedCharName, validInfo.charNameProperty().get());
    }

    @Test
    void serverNameProperty() {
        assertEquals(expectedServer, validInfo.serverNameProperty().get());
    }

    @Test
    void dateProperty() {
        assertEquals(expectedDate, validInfo.dateProperty().get().toString());
    }

    @Test
    void timeProperty() {
        assertEquals(expectedTime, validInfo.timeProperty().get().toString());
    }

    @Test
    void startLineProperty() {
        assertEquals(expectedLine, validInfo.startLineProperty().get());
    }

    @Test
    void testEquals() {
        SessionInfo newSessionInfo = new SessionInfo(expectedID, expectedLine);
        newSessionInfo.setContentProperties(expectedCharName, expectedServer, testCalendar, 0);
        assertEquals(validInfo, newSessionInfo);
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
        String expectedString = "SessionInfo{" +
                "sessionID=" + expectedID +
                ", startLine=" + expectedLine +
                ", charName=" + expectedCharName +
                ", serverName=" + expectedServer +
                ", date=" + expectedDate +
                ", time=" + expectedTime +
                '}';
        assertEquals(expectedString, validInfo.toString());
    }

}