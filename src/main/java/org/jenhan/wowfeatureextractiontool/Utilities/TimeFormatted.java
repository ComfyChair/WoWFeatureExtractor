package org.jenhan.wowfeatureextractiontool.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

// Utility class to display time in javafx tableview (overwritten toString function)
public class TimeFormatted extends Date {
    String formattedTime;

    public TimeFormatted(Date date)
    {
        this.formattedTime = new SimpleDateFormat("HH:mm:ss").format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TimeFormatted that = (TimeFormatted) o;
        return Objects.equals(formattedTime, that.formattedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), formattedTime);
    }

    @Override
    public String toString() {
        return formattedTime;
    }
}
