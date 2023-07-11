package org.jenhan.wowfeatureextractiontool.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/** Utility class to display the date in javafx tableview by overwriting toString function **/
public class DateFormatted extends Date {
    /** formatted String representation of the date **/
    private final String formattedDate;

    /** constructor
     * @param date the date that is to be formatted **/
    public DateFormatted(Date date) {
        this.formattedDate =  new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateFormatted that = (DateFormatted) o;
        return this.formattedDate.equals(that.formattedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formattedDate);
    }

    @Override
    public String toString() {
        return formattedDate;
    }
}
