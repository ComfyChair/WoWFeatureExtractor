package org.jenhan.wowfeatureextractiontool.Utilities;

import org.jenhan.wowfeatureextractiontool.SessionInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DateFormatted extends Date {
    Date date;

    public DateFormatted(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateFormatted that = (DateFormatted) o;
        return Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }
}
