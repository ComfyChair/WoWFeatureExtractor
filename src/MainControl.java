import java.util.ArrayList;

public class MainControl {
    private String pathToAddonFolder;
    private String pathToSavedVariables;
    private SessionManager sessionManager;
    ArrayList<Session.SessionInfo> sessionInfos;

    private MainControl(){}

    public static void  main(String[] args)
    {

    }

    void installAddon(){
        // TODO: implement addon installation procedure
    }

    void selectAddonFolder(String path){
        // receive installation folder from GUI
        pathToAddonFolder = path;
    }

    void selectSavedVarFolder(String path){
        // receive installation folder from GUI
        pathToSavedVariables = path;
    }

    void selectSession(int sessionID){
        // receive session selection from GUI
        sessionManager = SessionManager.getInstance();
        sessionManager.exportToXML(sessionID);
    }

    void exportToXML(){
        if (pathToSavedVariables == null ){
            // TODO: prompt GUI for installation folder
        } else {
            sessionManager = SessionManager.getInstance();
            sessionInfos = sessionManager.getSessionList();
            if (sessionInfos.isEmpty()){ // no session recorded
                // TODO: error message: no session found
            } else if (sessionInfos.size() > 1) { // if more than 1 session, session selection is required
                // TODO: prompt GUI for session selection
            } else  { // only 1 session -> export
                sessionManager.exportToXML(0);
            }
        }
    }
}
