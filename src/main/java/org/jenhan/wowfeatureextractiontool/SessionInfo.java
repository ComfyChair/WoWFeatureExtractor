package org.jenhan.wowfeatureextractiontool;

import javafx.beans.property.*;
import javafx.fxml.FXML;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

// session info data structure
// core information as needed for session selection display plus file reading information (startLine)
public class SessionInfo {
    @FXML
    private final IntegerProperty sessionID = new SimpleIntegerProperty();
    @FXML
    private final IntegerProperty startLine = new SimpleIntegerProperty();
    @FXML
    private final StringProperty charName = new SimpleStringProperty();
    @FXML
    private final StringProperty serverName = new SimpleStringProperty();
    @FXML
    private final ObjectProperty<DateFormatted> date = new SimpleObjectProperty<>();
    @FXML
    private final ObjectProperty<TimeFormatted> time = new SimpleObjectProperty<>();

    SessionInfo(int sessionID, int startLine,
                String charName, String serverName, Calendar calendar) {
        this.sessionID.setValue(sessionID);
        this.startLine.setValue(startLine);
        this.charName.setValue(charName);
        this.serverName.setValue(serverName);
        this.date.setValue(new DateFormatted(calendar.getTime()));
        this.time.setValue(new TimeFormatted(calendar.getTime()));
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

    public ObjectProperty<DateFormatted> dateProperty() {
        return date;
    }

    public ObjectProperty<TimeFormatted> timeProperty() {
        return time;
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
                && this.time.get().toString().equals(that.time.get().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID.get(), startLine.get(), charName.get(), serverName.get(), time.get());
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionID=" + sessionID +
                ", startLine=" + startLine +
                ", charName=" + charName +
                ", serverName=" + serverName +
                ", dateTime=" + time +
                '}';
    }

    public static class DateFormatted extends Date {
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

    public static class TimeFormatted extends Date {
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

}
