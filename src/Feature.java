import java.util.ArrayList;

public class Feature {
    long time; // UNIX time
    FeatureType type;
    String description;
    ArrayList<Object> objectList;

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

        private String description;

        FeatureType(String description){
            this.description = description;
        }

        String getDescription(){
            return description;
        }
    }
}
