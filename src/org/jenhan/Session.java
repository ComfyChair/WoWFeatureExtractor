package org.jenhan;

import java.io.File;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Session implements LuaToXML {
    private SessionInfo sessionInfo;

    Session(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }

    SessionInfo getSessionInfo(){
        return sessionInfo;
    }

    record SessionInfo(int lineStart, int lineStop, int sessionID,
                       String charName, String serverName, Calendar time){}

    // Utility for org.jenhan.SessionManager class
    static List<SessionInfo> readSessionInfo(File luaFile) {
        LineNumberReader luaReader = LuaToXML.getNumberReader(luaFile);
        List<Session.SessionInfo> sessionList = new ArrayList<>();
        //TODO: read the lua file, find sessions, populate sessionInfoList
        return sessionList;
    }
}
