package org.jenhan.wowfeatureextractiontool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// data structure for writing feature data
public class Feature {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Calendar calendar;
    FeatureType type;
    String description;
    List<FeatureObject> objectList = new ArrayList<>();

    record FeatureObject(int id, String term){}
    enum FeatureType{
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

        FeatureType(String description){
            this.standardDescription = description;
        }

        public String getStandardDescription() {
            return standardDescription;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) { // takes time in seconds, stores Unix time (=in milliseconds)
        this.calendar = calendar;
    }

    public FeatureType getType() {
        return type;
    }

    public void setType(String typeString) {
        for (FeatureType type : FeatureType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                this.type = type;
            }
        }
        if (this.type == null){
            log.logp(Level.WARNING,"Feature", "setType", "Unknown feature type: " + typeString);
            this.type = FeatureType.UNKNOWN;
        }
    }

    public List<FeatureObject> getObjectList() {
        return objectList;
    }

    public void addObject(FeatureObject object) {
        this.objectList.add(object);
    }

}
