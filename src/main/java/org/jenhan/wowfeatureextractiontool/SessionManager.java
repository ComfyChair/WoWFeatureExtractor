package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/** Session Management Singleton **/
public class SessionManager {
    private static final String OUTFILE_NAME = "WoW_Features";
    private static final String OUTFILE_EXTENSION = ".xml";
    private static SessionManager instance;
    private List<Session> sessionList;

    private SessionManager() {
    }

    static SessionManager getInstance() {
        // "lazy" initialization: delay initialization until needed
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    List<Session> getSessionList(File luaFile) {
        LuaReader luaReader = new LuaReader();
        sessionList = luaReader.readFile(luaFile);
        return sessionList;
    }

    int exportToXML(File outPath, List<Integer> sessionIDs) {
        String fileName;
        int count = 0;
        if (sessionIDs.size() == 1) { // single session, no additional identifier for output
            fileName = OUTFILE_NAME + OUTFILE_EXTENSION;
            File outFile = extendPath(outPath.toPath(), fileName);
            count = sessionList.get(sessionIDs.get(0)).exportToXML(outFile) ? count + 1 : count;
        } else { // multiple session, append session id to file name
            for (Integer sessionID : sessionIDs
            ) {
                fileName = OUTFILE_NAME + "_" + sessionID + OUTFILE_EXTENSION;
                File outFile = extendPath(outPath.toPath(), fileName);
                count = sessionList.get(sessionID).exportToXML(outFile) ? count + 1 : count;
            }
        }
        return count;
    }

    private File extendPath(Path path, String fileName) {
        return path.resolve(fileName).toFile();
    }

}
