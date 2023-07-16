package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import org.jenhan.wowfeatureextractor.Util.DateFormatted;
import org.jenhan.wowfeatureextractor.Util.TimeFormatted;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/** Recording session class containing general information and list of features **/
@XmlRootElement(name = LuaToXML.GMAF_DATA)
@XmlType(propOrder = {"fileName", LuaToXML.DATE, "featureList"})
public class Session implements LuaToXML{
    private final List<Feature> featureList = new ArrayList<>();
    private String fileName;
    /** session information that gets displayed in the session picker, as properties (necessary for javafx) **/
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

    /** private constructor; default constructor necessary for javafx table view **/
    private Session() {
    }

    /** creates a new session instance
     * @param sessionID the session id
     * @param fileName the filename
     * @return returns the new session **/
    static Session create(int sessionID, String fileName) {
        Session session = new Session();
        session.sessionID.setValue(sessionID);
        session.fileName = fileName;
        return session;
    }

    /** converts the session content to a GMAF-compatible xml **/
    @Override
    public boolean exportToXML(File outputFile) {
        Converter converter = new Converter(this);
        // pass to default interface method
        return converter.exportToXML(outputFile);
    }

   /** @return the character name property **/
    public IntegerProperty sessionIDProperty() {
        return sessionID;
    }

    /** @return the character name property, necessary for javafx **/
    public StringProperty charNameProperty() {
        return charName;
    }

    /** @return the server name property, necessary for javafx **/
    public StringProperty serverNameProperty() {
        return serverName;
    }

    /** @return the formatted date property, necessary for javafx **/
    public ObjectProperty<DateFormatted> dateProperty() {
        return date;
    }

    /** @return the formatted time property, necessary for javafx **/
    public ObjectProperty<TimeFormatted> startTimeProperty() {
        return startTime;
    }

    /** @return the formatted date string, necessary for jaxb bindings **/
    @XmlElement(name = LuaToXML.DATE)
    public String getDate() {
        return date.get().toString();
    }

    /** @return the file name string, necessary for jaxb bindings **/
    @XmlElement(name = LuaToXML.FILE)
    public String getFileName() {
        return fileName;
    }

    /** @return the feature list, necessary for jaxb bindings **/
    @XmlElements(@XmlElement(name = LuaToXML.INTERACTION))
    List<Feature> getFeatureList() {
        return featureList;
    }

    /** sets the character name property **/
    void setCharName(String charName) {
        this.charName.set(charName);
    }

    /** sets the server name property **/
    void setServerName(String serverName) {
        this.serverName.set(serverName);
    }

    /** sets the date and time properties **/
    void setDateTime(Date date) {
        this.date.set(new DateFormatted(date));
        this.startTime.set(new TimeFormatted(date));
    }

    /** adds a feature to the feature list
     * @param feature feature to be added **/
    void addFeature(Feature feature) {
        this.featureList.add(feature);
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

}
