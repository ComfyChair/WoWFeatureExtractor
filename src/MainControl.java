import java.io.File;
import java.util.List;

public class MainControl {
    //"eager" initialization, as the control class is needed right away
    private static final MainControl instance = new MainControl();
    // paths
    private String pathToAddonFolder;
    private File inputFile;
    private File outputFile;
    // session stuff
    private SessionManager sessionManager;
    List<Session.SessionInfo> sessionInfos;

    private MainControl() {
    }

    public static MainControl getInstance() {
        return instance;
    }

    void installAddon() {
        // TODO: implement addon installation procedure
    }

    void selectAddonFolder(String path) {
        // receive installation folder from GUI
        pathToAddonFolder = path;
        // TODO: check for validity!
        // TODO: navigate to SavedVars from Addon folder and set inputFile variable
    }

    void selectSavedVarFolder(String path) { // receives installation folder from GUI
        inputFile = new File(path);
    }

    void selectSession(int sessionID) { // receive session selection from GUI
        sessionManager = SessionManager.getInstance();
        sessionManager.exportToXML(inputFile, outputFile, sessionID);
    }

    void exportToXML(String outPath) {
        String outPathComplete = outPath + "/out.xml";
        outputFile = new File(outPathComplete);
        if (inputFile == null) {
            // TODO: prompt GUI for installation folder
        } else {
            sessionManager = SessionManager.getInstance();
            sessionInfos = sessionManager.getSessionList(inputFile);
            if (sessionInfos.isEmpty()) { // no session recorded
                // TODO: error message: no session found
            } else if (sessionInfos.size() > 1) { // if more than 1 session, session selection is required
                // TODO: prompt GUI for session selection
            } else { // only 1 session -> export
                sessionManager.exportToXML(inputFile, outputFile, 0);
            }
        }
    }
}
