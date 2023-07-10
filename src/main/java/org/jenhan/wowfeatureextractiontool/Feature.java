package org.jenhan.wowfeatureextractiontool;

import org.jenhan.wowfeatureextractiontool.Utilities.TimeFormatted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.*;

// data structure for writing feature data
@XmlRootElement(name = LuaToXML.INTERACTION)
@XmlType(propOrder = {LuaToXML.TYPE, LuaToXML.DESCRIPTION, LuaToXML.OBJECT })
public class Feature {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private TimeFormatted beginTime = null;
    private FeatureType type = null;
    private String description = null;
    private final List<FeatureObject> objectList = new ArrayList<>();

    @XmlElement(name = LuaToXML.DESCRIPTION)
    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    TimeFormatted getBeginTime() {
        return beginTime;
    }

    void setBeginTime(Date beginDate) {
        this.beginTime = new TimeFormatted(beginDate);
    }

    @XmlElement(name = LuaToXML.TYPE)
    FeatureType getType() {
        return type;
    }

    public void setType(String typeString) {
        for (FeatureType type : FeatureType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                this.type = type;
            }
        }
        if (this.type == null) {
            log.logp(Level.WARNING, "Feature", "setType", "Unknown feature type: " + typeString);
            this.type = FeatureType.UNKNOWN;
        }
    }

    boolean isComplete(){
        return beginTime != null && type != null && description != null;
    }

    @XmlElements(@XmlElement(name = LuaToXML.OBJECT))
    List<FeatureObject> getObjectList() {
        return objectList;
    }

    void addObject(FeatureObject object) {
        this.objectList.add(object);
    }

    enum FeatureType {
        MOV_1("started moving"),
        MOV_2("stopped moving"),
        ZONE_1("entered new area"),
        ZONE_2("entered new subarea"),
        ZONE_3("entered indoor area"),
        OBJ_1("looting"),
        OBJ_2("opened mailbox"),
        NPC_1("quest npc"),
        NPC_2("flight master"),
        COMM_1("emote"),
        COMM_2("whisper"),
        COMM_3("party chat"),
        GRP_1("grouping: group composition update"),
        SPELL_1("sent spellcast"),
        UNKNOWN("unknown type");

        private final String standardDescription;

        FeatureType(String description) {
            this.standardDescription = description;
        }

    }

    @XmlRootElement(name = LuaToXML.OBJECT)
    @XmlType(propOrder = {LuaToXML.ID, LuaToXML.TERM, LuaToXML.PROBABILITY })
    record FeatureObject(int id, String term, double probalility) {
        @XmlElement(name = LuaToXML.ID)
        int getId(){
            return id;
        }
        @XmlElement(name = LuaToXML.TERM)
        String getTerm(){
            return term;
        }
        @XmlElement(name = LuaToXML.PROBABILITY)
        double getProbability(){
            return probalility;
        }
    }

    @Override
    public String toString() {
        return "Feature{" +
                "beginTime=" + beginTime +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", objectList=" + objectList +
                '}';
    }
}
