package org.jenhan;

import java.util.ArrayList;

public class Feature {
    // not sure why I created this - let's see later if we can just delete it or extract the enum to org.jenhan.LuaToXML interface

    long time; // UNIX time
    FeatureType type;
    ArrayList<Object> objectList;

    Feature(long time, FeatureType type, ArrayList<Feature.Object> objectList){
        this.time = time;
        this.type = type;
        this.objectList = objectList;
    }

    record Object(int id, String term){}
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

        String getDescription(){
            return description;
        }
    }
}
