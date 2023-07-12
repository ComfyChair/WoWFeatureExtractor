package org.jenhan.wowfeatureextractiontool.Util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DateFormattedTest {
    Calendar testCalendar;
    Calendar anotherDayCalendar;
    Calendar anotherMonthCalendar;
    Calendar anotherYearCalendar;
    List<Calendar> compareCalendars = new ArrayList<>();
    DateFormatted testFormatted;

    @BeforeEach
    void setUp() {
        testCalendar = new GregorianCalendar();
        // TODO: randomize test date
        testFormatted = new DateFormatted(testCalendar.getTime());

        anotherDayCalendar = new GregorianCalendar(testCalendar.get(Calendar.YEAR),
                testCalendar.get(Calendar.MONTH), (testCalendar.get(Calendar.DAY_OF_MONTH) + 1) % 28 );
        int monthOrdinal = (testCalendar.get(Calendar.MONTH) + 1) % 12;
        anotherMonthCalendar = new GregorianCalendar(testCalendar.get(Calendar.YEAR),
                 monthOrdinal, testCalendar.get(Calendar.DAY_OF_MONTH));
        anotherYearCalendar = new GregorianCalendar(testCalendar.get(Calendar.YEAR) + 1 ,
                testCalendar.get(Calendar.MONTH), testCalendar.get(Calendar.DAY_OF_MONTH) );
        Collections.addAll(compareCalendars, anotherDayCalendar, anotherMonthCalendar, anotherYearCalendar);
    }

    @Test
    void testEquals() {
        DateFormatted sameTime = new DateFormatted(testCalendar.getTime());
        assertEquals(sameTime, testFormatted);
        for (Calendar anotherCalendar: compareCalendars
             ) {
            DateFormatted anotherTime = new DateFormatted(anotherCalendar.getTime());
            assertNotEquals(anotherTime, testFormatted);
        }
    }

    @Test
    void testHashCode() {
        DateFormatted sameTime = new DateFormatted(testCalendar.getTime());
        assertEquals(sameTime.hashCode(), testFormatted.hashCode());
        for (Calendar anotherCalendar: compareCalendars
        ) {
            DateFormatted anotherTime = new DateFormatted(anotherCalendar.getTime());
            assertNotEquals(anotherTime.hashCode(), testFormatted.hashCode());
        }
    }

    @Test
    void testToString() {
        String pattern = "dd.MM.yyyy";
        String expected = new SimpleDateFormat(pattern).format(testCalendar.getTime());
        assertEquals(expected, testFormatted.toString());
        for (Calendar anotherCalendar: compareCalendars
        ) {
            DateFormatted anotherTime = new DateFormatted(anotherCalendar.getTime());
            assertNotEquals(anotherTime.toString(), testFormatted.toString());
        }
    }
}