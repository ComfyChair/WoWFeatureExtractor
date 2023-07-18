package org.jenhan.wowfeatureextractor;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Session Management Singleton **/
class SessionManager {
    private static SessionManager instance;
    private List<Session> sessionList;

    /** Singletons keep their constructor private **/
    private SessionManager() {
    }

    /** @return the Singleton instance **/
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

    /** initiates export of the selected session(s) to xml
     * @param outFile outputFile
     * @param sessionIDs List of session ids = position in internal session list
     * @return List of output files **/
    List<File> exportToXML(File outFile, List<Integer> sessionIDs) {
        List<File> outList = new ArrayList<>();
        if (sessionIDs.size() == 1) { // single session, no additional identifier for output
            exportSingleSession(sessionIDs.get(0), outFile, outList);
        } else { // multiple session, append session id to file name
            String outPath = outFile.toPath().toString();
            for (Integer sessionID : sessionIDs
            ) {
                File thisOutFile  = Path.of(outPath.replace(".xml", "_" + sessionID + ".xml")).toFile();
                exportSingleSession(sessionID, thisOutFile, outList);
            }
        }
        return outList;
    }

    /** calls exportToXML() on the specified session and adds the output file to a List **/
    private void exportSingleSession(int sessionID, File outFile, List<File> outList) {
        boolean success = sessionList.get(sessionID).exportToXML(outFile);
        if (success){
            outList.add(outFile);
        }
    }

}
