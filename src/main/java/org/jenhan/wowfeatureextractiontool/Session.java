package org.jenhan.wowfeatureextractiontool;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import org.jenhan.wowfeatureextractiontool.Utilities.DateFormatted;
import org.jenhan.wowfeatureextractiontool.Utilities.TimeFormatted;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Session {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private List<Feature> featureList = new ArrayList<>();
    // session info displayed in session picker as properties (necessary for javafx)
    @FXML
    private final IntegerProperty sessionID = new SimpleIntegerProperty();
    @FXML
    private final StringProperty charName = new SimpleStringProperty();
    @FXML
    private final StringProperty serverName = new SimpleStringProperty();
    @FXML
    private final ObjectProperty<DateFormatted> date = new SimpleObjectProperty<>();
    @FXML
    private final ObjectProperty<TimeFormatted> startTime = new SimpleObjectProperty<>();

    // constructor
    Session(int sessionID)
    {
        this.sessionID.setValue(sessionID);
    }

    // converts lua feature table to GMAF-style xml
    boolean exportToXML(File outputFile) {
        LuaToXMLConverter converter = new LuaToXMLConverter(this);
        // pass to default interface method
        return converter.exportToXML(outputFile);
    }

    // these getters have to be public so that Javafx can access them
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
    public ObjectProperty<TimeFormatted> startTimeProperty() {
        return startTime;
    }

    List<Feature> getFeatureList() {
        return featureList;
    }

    void setCharName(String charName) {
        this.charName.set(charName);
    }

    void setServerName(String serverName) {
        this.serverName.set(serverName);
    }

    void setDateTime(Date date) {
        this.date.set(new DateFormatted(date));
        this.startTime.set(new TimeFormatted(date));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session that = (Session) o;
        return (this.sessionID.get() == that.sessionID.get())
                && this.charName.get().equals(that.charName.get())
                && this.serverName.get().equals(that.serverName.get())
                && this.startTime.get().toString().equals(that.startTime.get().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID.get(), charName.get(), serverName.get(), startTime.get());
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionID=" + sessionID.get() +
                ", charName=" + charName.get() +
                ", serverName=" + serverName.get() +
                ", date=" + date.get() +
                ", time=" + startTime.get() +
                '}';
    }

    public void addFeature(Feature feature) {
        this.featureList.add(feature);
    }
}
