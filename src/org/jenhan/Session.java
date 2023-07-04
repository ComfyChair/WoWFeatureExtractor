package org.jenhan;

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
    /* constants */
    private static final String SESSION_START = "[\"session_";
    private static final String CHAR_NAME = "characterName";
    private static final String SERVER_NAME = "serverName";
    private static final String START_TIME = "startTimeStamp";
    private static final String FEATURE_TABLE = "featureTable";

    // object attribute record
    private SessionInfo sessionInfo;

    Session(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    record SessionInfo(int sessionID, int startLine,
                       String charName, String serverName, Calendar time) {
    }

    // Utility method for displaying session info to the user prior to conversion
    static List<SessionInfo> readSessionInfo(File luaFile) {
        int sessionID = 0;
        LineNumberReader luaReader = LuaToXML.getReader(luaFile);
        List<Session.SessionInfo> sessionList = new ArrayList<>();
        String line;
        try {
            while (((line = luaReader.readLine()) != null)) { // null marks the end of the stream
                line = line.trim();
                if (line.startsWith(SESSION_START)) {
                    // memorize start line number
                    int startLine = luaReader.getLineNumber(); // points to first entry, not to the ["session_X"] line
                    log.fine("found session, line number: " + startLine);
                    String charName = null;
                    String serverName = null;
                    Calendar startTime = null;
                    // read session lines until all info fields are populated
                    while ((charName == null) || (serverName == null) || (startTime == null)) {
                        line = luaReader.readLine();
                        log.fine("Read line: " + line);
                        if (LuaToXML.isAssignment(line)) {
                            String fieldKey = LuaToXML.getLuaFieldKey(line);
                            log.fine("is assignment with key: " + fieldKey);
                            switch (fieldKey) {
                                case CHAR_NAME -> charName = LuaToXML.getLuaFieldValue(line);
                                case SERVER_NAME -> serverName = LuaToXML.getLuaFieldValue(line);
                                case START_TIME -> {
                                    long unixStartTime = Long.parseLong(LuaToXML.getLuaFieldValue(line));
                                    // convert UNIX time to Calendar
                                    startTime = Calendar.getInstance();
                                    startTime.setTime(new Date(unixStartTime));
                                }
                                default -> log.fine("line not relevant");
                            }
                        }
                    }
                    SessionInfo newSessionInfo = new SessionInfo(sessionID, startLine, charName, serverName, startTime);
                    sessionList.add(newSessionInfo);
                    log.info("Added session: " + newSessionInfo);
                }
            }
        } catch (IOException e) {
            //TODO: handle IOException
            throw new RuntimeException(e);
        }
        return sessionList;

    }

    @Override
    public boolean exportToXML(File inputFile, File outputFile) {
        boolean success = false;
        LineNumberReader luaReader = LuaToXML.getReader(inputFile);
        PrintWriter xmlWriter = LuaToXML.getWriter(outputFile);
        try {
            writeDate(xmlWriter, sessionInfo.time);
            skipToStartLine(luaReader);
            skipToFeatureTable(luaReader);
            xmlWriter.flush();
            xmlWriter.close();
            return success;
        } catch (IOException e) {
            //TODO: handle exception
            log.severe("Error while converting");
            return false;
        }
    }

    private void writeDate(PrintWriter xmlWriter, Calendar time) {
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String recordingDate = simpleDateFormat.format(time.getTime());
        xmlWriter.write(GMAF_DATE.getOpenTag() + recordingDate + GMAF_DATE.getCloseTag());
    }

    private static void skipToFeatureTable(LineNumberReader luaReader) throws IOException {
        boolean found = false;
        String nextLine;
        while (!found && (nextLine = luaReader.readLine()) != null){
            if (LuaToXML.isAssignment(nextLine) && LuaToXML.getLuaFieldKey(nextLine) == FEATURE_TABLE){
                found = true;
            }
        }
    }

    private void skipToStartLine(LineNumberReader luaReader) throws IOException {
        for (int i = 0; i < this.sessionInfo.startLine; i++) {
            luaReader.readLine();
        }
    }

}
