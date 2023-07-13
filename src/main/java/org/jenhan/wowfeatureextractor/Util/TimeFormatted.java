package org.jenhan.wowfeatureextractor.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Utility class to display time in javafx tableview (overwritten toString function) **/
public class TimeFormatted extends Date {
    /** formatted String representation of the time **/
    private final String formattedTime;

    /** constructor
     * @param date date object from which the time portion is to be formatted **/
    public TimeFormatted(Date date)
    {
        this.formattedTime = new SimpleDateFormat("HH:mm:ss").format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeFormatted that = (TimeFormatted) o;
        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        return formattedTime.hashCode();
    }

    @Override
    public String toString() {
        return formattedTime;
    }
}
