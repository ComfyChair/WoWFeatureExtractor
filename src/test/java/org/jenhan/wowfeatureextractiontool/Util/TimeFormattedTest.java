package org.jenhan.wowfeatureextractiontool.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TimeFormattedTest {
    Date testDate;
    Date anotherDate;
    TimeFormatted testTime;

    @BeforeEach
    void setUp() {
        Random rnd = new Random();
        long unixTime = rnd.nextLong();
        testDate = new Date(unixTime);
        anotherDate = new Date(unixTime + 10000L);
        testTime = new TimeFormatted(testDate);
    }

    @Test
    void testEquals() {
        TimeFormatted sameTime = new TimeFormatted(testDate);
        TimeFormatted anotherTime = new TimeFormatted(anotherDate);
        assertEquals(sameTime, testTime);
        assertNotEquals(anotherTime, testTime);

    }

    @Test
    void testHashCode() {
        TimeFormatted sameTime = new TimeFormatted(testDate);
        TimeFormatted anotherTime = new TimeFormatted(anotherDate);
        assertEquals(sameTime.hashCode(), testTime.hashCode());
        assertNotEquals(anotherTime.hashCode(), testTime.hashCode());
    }

    @Test
    void testToString() {
        String expected = new SimpleDateFormat("HH:mm:ss").format(testDate);
        assertEquals(expected, testTime.toString());
    }
}