package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Session Management Singleton **/
class SessionManager {
    private static final String OUTFILE_NAME = "WoW_Features";
    private static final String OUTFILE_EXTENSION = ".xml";
    private static SessionManager instance;
    private List<Session> sessionList;

    /** private constructor **/
    private SessionManager() {
    }

    /** @return the one and only instance **/
    static SessionManager getInstance() {
        // "lazy" initialization: delay initialization until needed
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /** @return the list of all the sessions in the given file
     * @param luaFile the input file **/
    List<Session> getSessionList(File luaFile) {
        LuaReader luaReader = new LuaReader();
        sessionList = luaReader.readFile(luaFile);
        return sessionList;
    }

    /** initiates export of the selected session to xml
     * @param outPath outputFile
     * @param sessionIDs List if session ids = position in internal session list
     * @return number of converted sessions **/
    List<File> exportToXML(File outPath, List<Integer> sessionIDs) {
        String fileName;
        List<File> outList = new ArrayList<>();
        if (sessionIDs.size() == 1) { // single session, no additional identifier for output
            fileName = OUTFILE_NAME + OUTFILE_EXTENSION;
            exportSingleSession(0, outPath, fileName, outList);
        } else { // multiple session, append session id to file name
            for (Integer sessionID : sessionIDs
            ) {
                fileName = OUTFILE_NAME + "_" + sessionID + OUTFILE_EXTENSION;
                exportSingleSession(sessionID, outPath, fileName, outList);
            }
        }
        return outList;
    }

    private void exportSingleSession(int sessionID, File outPath, String fileName, List<File> outList) {
        File outFile = extendPath(outPath.toPath(), fileName);
        boolean success = sessionList.get(sessionID).exportToXML(outFile);
        if (success){
            outList.add(outFile);
        }
    }

    /** extends the path with a given file name
     * @param path the path
     * @param fileName the file name **/
    private File extendPath(Path path, String fileName) {
        return path.resolve(fileName).toFile();
    }

}
