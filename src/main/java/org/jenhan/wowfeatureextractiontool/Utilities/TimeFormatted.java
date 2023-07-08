package org.jenhan.wowfeatureextractiontool.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class TimeFormatted extends Date {
    Date date;

    public TimeFormatted(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TimeFormatted that = (TimeFormatted) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }
}
