import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private List<Session> sessionList = new ArrayList<>();

    private SessionManager(){}

    public static SessionManager getInstance() {
        // "lazy" initialization (initialize if needed)
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    List<Session.SessionInfo> getSessionList(File luaFile){
        // TODO: populate session list by reading lua file and creating sessions
        sessionList = LuaToXML.readSessionInfo(luaFile);
        List<Session.SessionInfo> sessionInfos = new ArrayList<>();
        for (Session session: sessionList
             ) {
            Session.SessionInfo sessionInfo = session.getSessionInfo();
            sessionInfos.add(sessionInfo);
        }
        return sessionInfos;
    }

    void exportToXML(File inPath, File outPath, int sessionID){
        sessionList.get(sessionID).exportToXML(inPath, outPath);
    }

}
