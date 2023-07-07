package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private final List<Session> sessionList = new ArrayList<>();

    private SessionManager(){}

    public static SessionManager getInstance() {
        // "lazy" initialization (initialize if needed)
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    List<SessionInfo> getSessionList(File luaFile){
        // get session information from file (static call, thus handled by interface class)
        List<SessionInfo> sessionInfos = Session.readSessionInfo(luaFile);
        // create Session objects from session info
        for (SessionInfo sessionInfo: sessionInfos
             ) {
            Session newSession = new Session(sessionInfo);
            sessionList.add(newSession);
        }
        return sessionInfos;
    }

    void exportToXML(File inPath, File outPath, List<Integer> sessionIDs){
        if (sessionIDs.size() == 1){ // single session, no additional identifier for output
            sessionList.get(sessionIDs.get(0)).exportToXML(inPath, outPath);
        } else { // multiple session, append session id to file name
            for (Integer sessionID: sessionIDs
            ) {
                outPath = new File(outPath + "_" + sessionID);
                sessionList.get(sessionID).exportToXML(inPath, outPath);
            }
        }
    }

}
