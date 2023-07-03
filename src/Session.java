
import java.util.Calendar;

public class Session implements LuaToXML{
    private SessionInfo sessionInfo;

    SessionInfo getSessionInfo(){
        return sessionInfo;
    }

    record SessionInfo(int sessionID, String charName, String serverName, Calendar time){}
}
