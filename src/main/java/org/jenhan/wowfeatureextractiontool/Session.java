package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Session implements LuaToXML {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    // session information record
    private final SessionInfo sessionInfo;

    // constructor
    Session(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    // reads session info from a recorded .lua file, so that it can be shown to the user in a session selection dialog
    static List<SessionInfo> readSessionInfo(File luaFile) {
        return LuaToXML.readSessionInfo(luaFile);
    }

    // returns info for a single session
    SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    // converts lua feature table to GMAF-style xml
    boolean exportToXML(File inputFile, File outputFile) {
        // pass to default interface method
        return exportToXML(inputFile, sessionInfo, outputFile);
    }

}
