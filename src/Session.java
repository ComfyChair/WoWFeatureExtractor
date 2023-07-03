
import java.util.ArrayList;
import java.util.Calendar;

public class Session implements LuaToXML{
    private SessionInfo sessionInfo;
    private ArrayList<Feature> featureList = new ArrayList<>();

    SessionInfo getSessionInfo(){
        return sessionInfo;
    }

    record SessionInfo(int sessionID, String charname, String serverName, Calendar time){}

}
