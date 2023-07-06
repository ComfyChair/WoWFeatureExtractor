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

    List<Session.SessionInfo> getSessionList(File luaFile){
        // get session information from file (static call, thus handled by interface class)
        List<Session.SessionInfo> sessionInfos = Session.readSessionInfo(luaFile);
        // create org.jenhan.Session objects from session info
        for (Session.SessionInfo sessionInfo: sessionInfos
             ) {
            Session newSession = new Session(sessionInfo);
            sessionList.add(newSession);
        }
        return sessionInfos;
    }

    void exportToXML(File inPath, File outPath, int sessionID){
        sessionList.get(sessionID).exportToXML(inPath, outPath);
    }

}
