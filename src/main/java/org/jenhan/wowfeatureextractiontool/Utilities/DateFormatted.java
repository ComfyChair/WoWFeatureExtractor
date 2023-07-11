package org.jenhan.wowfeatureextractiontool.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/** Utility class to display the date in javafx tableview by overwriting toString function **/
public class DateFormatted extends Date {
    String formattedDate;

    public DateFormatted(Date date) {
        this.formattedDate =  new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateFormatted that = (DateFormatted) o;
        return Objects.equals(formattedDate, that.formattedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), formattedDate);
    }

    @Override
    public String toString() {
        return formattedDate;
    }
}
