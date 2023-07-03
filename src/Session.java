
import java.util.ArrayList;
import java.util.Calendar;

public class Session implements LuaToXML{
    private SessionInfo sessionInfo;
    private ArrayList<Feature> featureList;

    SessionInfo getSessionInfo(){
        return sessionInfo;
    }

    @Override
    public boolean exportToXML(){
        boolean canRead = readFeaturesFromLua();
        boolean canWrite = writeXML();
        return canRead && canWrite;
    }

    private boolean readFeaturesFromLua() {
        featureList = new ArrayList<>();
        //TODO: populate featureList from Lua file
        return true;
    }

    private boolean writeXML() {
        // TODO: write XML from features
        return true;
    }

    record SessionInfo(int sessionID, String charname, String serverName, Calendar time){}

}
