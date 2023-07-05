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
        this.addonDir = directory;
        // TODO: navigate to SavedVars from Addon folder and set inputFile variable
        // TODO: implement addon installation procedure
    }

    void selectSavedVarFile(File inputFile) { // receives installation folder from GUI
        if (inputFile.exists()){
            if (inputFile.isDirectory()){
                String fileName = inputFile + "/" + ADDON_NAME + ".lua";
                inputFile = new File(fileName);
                if (!inputFile.exists()){
                    Gui.errorMessage("Error: There is no " + ADDON_NAME + ".lua file in the selected folder!");
                    return;
                } // else, go on
            }
            if (inputFile.canRead()){
                this.inputFile =  inputFile;
            } else {
                Gui.errorMessage("Error: Could not read the file: " + inputFile.getAbsolutePath());
            }
        }

    }

    void selectSession(int sessionID) { // receive session selection from GUI
        sessionManager = SessionManager.getInstance();
        sessionManager.exportToXML(inputFile, outputFile, sessionID);
    }

    void exportToXML(File outPath) {
        String outPathComplete = outPath.getName() + "/out.xml";
        outputFile = new File(outPathComplete);
        log.info("Output file will be saved to: " + outputFile.getAbsolutePath());
        if (inputFile == null) {
            inputFile = Gui.promptForFile("Please select the input file");
            if(inputFile == null){
                Gui.errorMessage("There is no valid input file");
                return;
            }
            if(!inputFile.canRead()){
                Gui.errorMessage("Can not read file: " + inputFile.getAbsolutePath());
                return;
            }
        }
            sessionManager = SessionManager.getInstance();
            sessionInfos = sessionManager.getSessionList(inputFile);
            if (sessionInfos.isEmpty()) { // no session recorded
                Gui.errorMessage("There was no recording found in the input file");
            } else if (sessionInfos.size() > 1) { // if more than 1 session, session selection is required
                Gui.promptForSession();
            } else { // only 1 session -> export without further ado
                sessionManager.exportToXML(inputFile, outputFile, 0);
            }

    }


}
