import java.util.ArrayList;

public class SessionManager {
    private static SessionManager instance;
    private static final ArrayList<Session.SessionInfo> sessionList = new ArrayList<>();

    private SessionManager(){}

    public static SessionManager getInstance() {
        // "lazy" initialization (initialize if needed)
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    ArrayList<Session.SessionInfo> getSessionList(){
        return sessionList;
    }

    void sessionFromLua(){

    }

    void exportToXML(int sessionID){

    }

}
