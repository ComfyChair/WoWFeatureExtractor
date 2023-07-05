package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MainControl {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    //"eager" initialization, as the control class is needed right away
    private static final MainControl instance = new MainControl();
    private static final String ADDON_NAME = "FeatureRecordingTool";
    // paths
    // TODO: persist folder locations
    private File addonDir;
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

    // unzips the addon files into the specified folder
    // receives installation directory from GUI
    void installAddon(File directory) {
        log.info("Installation directory: " + directory);
        this.addonDir = addonDir;
        // TODO: navigate to SavedVars from Addon folder and set inputFile variable
        // TODO: implement addon installation procedure
    }

    void selectSavedVarFolder(File directory) { // receives installation folder from GUI
        String fileName = directory.getName() + "/" + ADDON_NAME + ".lua";
        // TODO: check for existence of file
        inputFile =  new File(fileName);
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
            } else { // only 1 session -> export without further ado
                sessionManager.exportToXML(inputFile, outputFile, 0);
            }
        }
    }


}
