package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.annotation.*;
import org.jenhan.wowfeatureextractor.Util.TimeFormatted;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/** Data structure for writing feature data to xml **/
@XmlRootElement(name = LuaToXML.INTERACTION)
@XmlType(propOrder = {"beginTime", LuaToXML.TYPE, LuaToXML.DESCRIPTION, "objectList"})
class Feature {
    static final String DEFAULT_TYPE = "UNKNOWN";
    static final String DEFAULT_DESCR = "no description";
    private final List<FeatureObject> objectList = new ArrayList<>();
    private TimeFormatted beginTime = new TimeFormatted(new Date(0L));
    private String type = DEFAULT_TYPE;
    private String description = DEFAULT_DESCR;

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
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = LuaToXML.TYPE)
    String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
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

    /** Inner class for "relevant objects" of a feature **/
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

        void setId(int id) {
            this.id = id;
        }

        void setTerm(String term) {
            this.term = term;
        }

        void setProbability(double probability) {
            this.probability = probability;
        }
    }
}
