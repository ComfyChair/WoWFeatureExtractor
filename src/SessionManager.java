import java.util.ArrayList;

public class SessionManager {
    private static SessionManager instance;
    private static ArrayList<Session> sessionList = new ArrayList<>();

    private SessionManager(){}

    public static SessionManager getInstance() {
        // "lazy" initialization (initialize if needed)
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    ArrayList<Session.SessionInfo> getSessionList(){
        sessionList = sessionsFromLua();
        ArrayList<Session.SessionInfo> sessionInfos = new ArrayList<>();
        //TODO: compile Session info into ArrayList
        return sessionInfos;
    }

    ArrayList<Session> sessionsFromLua(){
        ArrayList<Session> sessionList = new ArrayList<>();
        // TODO: populate session list by reading lua file and creating sessions
        return sessionList;
    }

    void exportToXML(int sessionID){
        sessionList.get(sessionID).exportToXML();
    }

}
