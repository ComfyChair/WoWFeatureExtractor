package org.jenhan.wowfeatureextractiontool;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import org.jenhan.wowfeatureextractiontool.Utilities.DateFormatted;
import org.jenhan.wowfeatureextractiontool.Utilities.TimeFormatted;

import java.util.Calendar;
import java.util.Objects;

// session info data structure
// core information as needed for session selection display plus file reading information (startLine)
public class SessionInfo {
    @FXML
    private final IntegerProperty sessionID = new SimpleIntegerProperty();
    @FXML
    private final IntegerProperty startLine = new SimpleIntegerProperty();
    @FXML
    private final IntegerProperty startFeatureTable = new SimpleIntegerProperty();
    @FXML
    private final StringProperty charName = new SimpleStringProperty();
    @FXML
    private final StringProperty serverName = new SimpleStringProperty();
    @FXML
    private final ObjectProperty<DateFormatted> date = new SimpleObjectProperty<>();
    @FXML
    private final ObjectProperty<TimeFormatted> time = new SimpleObjectProperty<>();

    SessionInfo(int sessionID, int startLine) {
        this.sessionID.setValue(sessionID);
        this.startLine.setValue(startLine);
    }

    void setContentProperties(String charName, String serverName, Calendar calendar, int startFeatureTable){
        this.charName.setValue(charName);
        this.serverName.setValue(serverName);
        this.date.setValue(new DateFormatted(calendar.getTime()));
        this.time.setValue(new TimeFormatted(calendar.getTime()));
        this.startFeatureTable.setValue(startFeatureTable);
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
    IntegerProperty startFeatureTableProperty() {
        return startFeatureTable;
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
                "sessionID=" + sessionID.get() +
                ", startLine=" + startLine.get() +
                ", charName=" + charName.get() +
                ", serverName=" + serverName.get() +
                ", date=" + date.get() +
                ", time=" + time.get() +
                '}';
    }

}
