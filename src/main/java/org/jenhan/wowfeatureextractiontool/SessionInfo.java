package org.jenhan.wowfeatureextractiontool;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

// session info data structure
// core information as needed for session selection display plus file reading information (startLine)
public class SessionInfo {
    private final IntegerProperty sessionID = new SimpleIntegerProperty();
    private final IntegerProperty startLine = new SimpleIntegerProperty();
    private final StringProperty charName = new SimpleStringProperty();
    private final StringProperty serverName = new SimpleStringProperty();
    private final ObjectProperty<DateTimeFormatted> dateTime = new SimpleObjectProperty<>();

    SessionInfo(int sessionID, int startLine,
                String charName, String serverName, Calendar calendar) {
        this.sessionID.setValue(sessionID);
        this.startLine.setValue(startLine);
        this.charName.setValue(charName);
        this.serverName.setValue(serverName);
        this.dateTime.setValue(new DateTimeFormatted(calendar.getTime()));
    }

    public IntegerProperty sessionIDProperty() {
        return sessionID;
    }

    public StringProperty charNameProperty() {
        return charName;
    }

    public StringProperty serverNameProperty() {
        return serverName;
    }

    public ObjectProperty<DateTimeFormatted> dateTimeProperty() {
        return dateTime;
    }

    IntegerProperty startLineProperty() {
        return startLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionInfo that = (SessionInfo) o;
        return (this.sessionID.get() == that.sessionID.get())
                && this.startLine.get() == that.startLine.get()
                && this.charName.get().equals(that.charName.get())
                && this.serverName.get().equals(that.serverName.get())
                && this.dateTime.get().getDateString().equals(that.dateTime.get().getDateString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID.get(), startLine.get(), charName.get(), serverName.get(), dateTime.get());
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionID=" + sessionID +
                ", startLine=" + startLine +
                ", charName=" + charName +
                ", serverName=" + serverName +
                ", dateTime=" + dateTime +
                '}';
    }

    public static class DateTimeFormatted extends Date {
        Date dateTime;

        public DateTimeFormatted(Date date) {
            dateTime = date;
        }

        String getDateString(){
            return new SimpleDateFormat("dd.MM.yyyy").format(dateTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            DateTimeFormatted that = (DateTimeFormatted) o;
            return Objects.equals(dateTime, that.dateTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), dateTime);
        }

        @Override
        public String toString() {
            return new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(dateTime);
        }
    }

}
