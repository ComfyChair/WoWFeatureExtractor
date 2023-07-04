package org.jenhan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// data structure for writing feature data
public class Feature {
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
        SPELL_1("sent spellcast");

        private final String description;

        FeatureType(String description){
            this.description = description;
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

    public void setType(FeatureType type) {
        this.type = type;
    }

    public List<FeatureObject> getObjectList() {
        return objectList;
    }

    public void addObject(FeatureObject object) {
        this.objectList.add(object);
    }

}
