package org.jenhan.wowfeatureextractiontool;

import jakarta.xml.bind.annotation.*;
import org.jenhan.wowfeatureextractiontool.Util.TimeFormatted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/** Data structure for writing feature data to xml **/
@XmlRootElement(name = LuaToXML.INTERACTION)
@XmlType(propOrder = {"beginTime", LuaToXML.TYPE, LuaToXML.DESCRIPTION, "objectList"})
class Feature {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final List<FeatureObject> objectList = new ArrayList<>();
    private TimeFormatted beginTime = new TimeFormatted(new Date(0L));
    private FeatureType type = FeatureType.UNKNOWN;
    private String description = null;

    /** standard constructor, made explicit for jaxb binding **/
    Feature() {
    }

    /** adds feature objects to list **/
    void addObject(FeatureObject object) {
        this.objectList.add(object);
    }

    /** returns a formatted time string **/
    @XmlAttribute(name = LuaToXML.BEGIN)
    String getBeginTime() {
        return beginTime.toString();
    }

    void setBeginTime(Date beginDate) {
        this.beginTime = new TimeFormatted(beginDate);
    }

    @XmlElement(name = LuaToXML.DESCRIPTION)
    String getDescription() {
        String result;
        if (description != null) {
            result = description;
        } else {
            result = this.getType().standardDescription;
        }
        return result;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = LuaToXML.TYPE)
    FeatureType getType() {
        return type;
    }

    void setType(String typeString) {
        FeatureType newType = FeatureType.UNKNOWN;
        for (FeatureType type : FeatureType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                newType = type;
            }
        }
        this.type = newType;
    }

    @XmlElements(@XmlElement(name = LuaToXML.OBJECT))
    List<FeatureObject> getObjectList() {
        return objectList;
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

    /** enum data structure for allowed types with standard description **/
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

        String getStandardDescription(){
            return this.standardDescription;
        }
    }

    /** Inner class for objects that were detected in a feature **/
    @XmlRootElement(name = LuaToXML.OBJECT)
    @XmlType(propOrder = {LuaToXML.ID, LuaToXML.TERM, LuaToXML.PROBABILITY})
    static class FeatureObject {
        private int id;
        private String term;
        private double probability;

        /** standard constructor needed for jaxb binding **/
        FeatureObject() {
        }

        /** constructor
         * @param id id for xml output
         * @param term term for xml output **/
        FeatureObject(int id, String term) {
            this.id = id;
            this.term = term;
            this.probability = 1.00;
        }

        /** needed for jaxb binding **/
        @XmlElement(name = LuaToXML.ID)
        int getId() {
            return id;
        }

        /** needed for jaxb binding **/
        @XmlElement(name = LuaToXML.TERM)
        String getTerm() {
            return term;
        }

        /** needed for jaxb binding **/
        @XmlElement(name = LuaToXML.PROBABILITY)
        double getProbability() {
            return probability;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FeatureObject) obj;
            return this.id == that.id &&
                    this.term. equals(that.term) &&
                    Double.doubleToLongBits(this.probability) == Double.doubleToLongBits(that.probability);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, term, probability);
        }

        @Override
        public String toString() {
            return "FeatureObject[" +
                    "id=" + id + ", " +
                    "term=" + term + ", " +
                    "probalility=" + probability + ']';
        }

    }
}
