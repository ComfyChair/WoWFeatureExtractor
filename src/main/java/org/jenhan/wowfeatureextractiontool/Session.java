package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Session {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    // session information record
    private final SessionInfo sessionInfo;
    private final LuaToXMLConverter converter = new LuaToXMLConverter();

    // constructor
    Session(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    // reads session info from a recorded .lua file, so that it can be shown to the user in a session selection dialog
    static List<SessionInfo> readSessionInfo(File luaFile) {
        return LuaReader.readSessionInfo(luaFile);
    }

    // returns info for a single session
    SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    // converts lua feature table to GMAF-style xml
    boolean exportToXML(File inputFile, File outputFile) {
        // pass to default interface method
        return converter.exportToXML(inputFile, sessionInfo, outputFile);
    }

}
