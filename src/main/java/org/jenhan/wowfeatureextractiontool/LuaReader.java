package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/** Reader class to read extracted features from SavedVariables file **/
 class LuaReader {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /** Lua table fields **/
    private final static String SESSION_FIELD_START = "[\"session_";
    private final static String CHAR_NAME = "characterName";
    private final static String SERVER_NAME = "serverName";
    private final static String START_TIME = "startTimeStamp";
    private final static String FEATURE_TABLE = "featureTable";
    private final static String DESCRIPTION = "description";
    private final static String TYPE = "type";
    private final static String FEATURE_TIMESTAMP = "timestamp";
    private final static String OBJECT_LIST = "objects";
    private final static String END_OF_SESSION  = "\t},";
    private final static String END_OF_FEATURE_TABLE = "\t\t},";
    private final static String END_OF_TABLE ="}";

    /** constructor **/
    LuaReader() {
    }

    /** reads the input file and creates session instances
     * @param luaFile input file
     * @return list of created session instances **/
    List<Session> readFile(File luaFile) {
        List<Session> sessionList = new ArrayList<>();
        String line;
        int sessionID = -1;
        try (LineNumberReader reader = new LineNumberReader(new FileReader(luaFile))){
            while ((line = reader.readLine()) != null && (!line.equals(END_OF_TABLE))) { // null marks the end of the stream
                log.fine("Reading a line of " + luaFile);
                line = line.trim();
                if (line.startsWith(SESSION_FIELD_START)) {
                    sessionID++;
                    Session newSession = Session.create(sessionID, luaFile.getName());
                    log.fine("found session, line number: " + reader.getLineNumber() + ", content of last read line: " + line);
                    readSingleSession(reader, line, newSession);
                    sessionList.add(newSession);
                    log.fine("Added session: " + newSession);
                }
            }
        } catch (IOException e) {
            MainControl.handleError("Could not read session information from " + luaFile.getAbsolutePath(), e);
        }
        return sessionList;
    }

    /** reads the data of a single session
     * @param reader the LineNumber reader
     * @param line content of last read line
     * @param session the newly created session instance **/
    private void readSingleSession(LineNumberReader reader, String line, Session session) throws IOException {
        log.fine("Reading new session, line " + reader.getLineNumber());
        while (line!= null && !line.equals(END_OF_SESSION)) {
            line = reader.readLine();
            log.fine("Read line: " + line);
            if (isAssignment(line)) {
                String fieldKey = getLuaFieldKey(line);
                log.fine("is assignment with key: " + fieldKey);
                switch (fieldKey) {
                    case CHAR_NAME -> session.setCharName(getLuaFieldValue(line));
                    case SERVER_NAME -> session.setServerName(getLuaFieldValue(line));
                    case START_TIME -> session.setDateTime(getDateFromLuaField(line));
                    case FEATURE_TABLE -> line = readFeatureTable(reader, session);
                    default -> log.fine("line not relevant: " + line);
                }
                log.fine("Session state: " + session);
            }
        }
    }

    /** reads lua file to add features to the current session's feature list
     * @param reader the line number reader
     * @param session the current session
     * @return last read line; should be END_OF_FEATURE_TABLE **/
    private String readFeatureTable(LineNumberReader reader, Session session) throws IOException {
        Feature feature;
        log.fine("Reading feature table");
        String line = reader.readLine();
        while (line != null && !line.equals(END_OF_FEATURE_TABLE)) {
                log.fine("GetNext Feature - current Line: " + line);
                if (isNextFeature(line)) {
                    session.addFeature(readFeature(reader));
                }
                line = reader.readLine();
        }
        return line;
    }

    /** detects the next feature from the line content
     * @param line current line of the lua file
     * @return true if the current line opens the next feature**/
    private boolean isNextFeature(String line) {
        return line.trim().equals("{");
    }

    /** reads feature properties from lua file
     * @param reader the LineNumberReader
     * @return the complete feature **/
    private Feature readFeature(LineNumberReader reader) throws IOException {
        log.fine("Reading feature");
        String line = reader.readLine();
        Feature feature = new Feature();
        while (!isEndOfFeature(line)) {
            log.fine("Read Feature - current line: " + line);
            if (isAssignment(line)) {
                String key = getLuaFieldKey(line);
                switch (key) {
                    case FEATURE_TIMESTAMP -> feature.setBeginTime(getDateFromLuaField(line));
                    case DESCRIPTION -> feature.setDescription(LuaReader.getLuaFieldValue(line));
                    case TYPE -> feature.setType(LuaReader.getLuaFieldValue(line));
                    case OBJECT_LIST -> readFeatureObjects(reader, feature);
                }
            }
            line = reader.readLine();
        }
        log.fine("Feature read: " + feature);
        return feature;
    }

    /** determines whether a String is an assignment
     * @param line the line **/
    private static boolean isAssignment(String line) {
        return line.contains("=");
    }

    /** extracts the value from a lua assignment
     * @param line the assignment String
     * @return the value of the assignment as a String; empty string if called on a non-assignment String  **/
    private static String getLuaFieldValue(String line) {
        String result = "";
        if (isAssignment(line)) {
            String[] split = line.split("=");
            // right hand side of line = value
            String valueSide = split[1].trim();
            // substring to omit trailing comma
            valueSide = valueSide.substring(0, valueSide.length() - 1);
            // remove quotation marks
            if (valueSide.startsWith("\"")) {
                valueSide = valueSide.substring(1, valueSide.length() - 1);
            }
            result = valueSide;
        }
        return result;
    }

    /** extracts the key from a lua assignment
     * @param line the assignment String
     * @return the key of the assignment as a String; empty string if called on a non-assignment String **/
    private static String getLuaFieldKey(String line) {
        String result = "";
        if (isAssignment(line)) {
            String[] split = line.split("=");
            // left hand side of line = key
            String keySide = split[0].trim();
            // substring to omit quotation marks and brackets
            result = keySide.substring(2, keySide.length() - 2);
        }
        return result;
    }

    /** removes additional characters from a lua table entry String
     * @param line the complete entry line
     * @return the naked entry String **/
    private String getEntryFromLuaTable(String line) {
        String trimmed = line.trim();
        String leftOfComma = trimmed.split(",")[0];
        return leftOfComma.replace("\"", "");
    }

    /** determines whether a line signals the end of a lua table
     * @param line the line **/
    private boolean isEndOfTable(String line) {
        return line.trim().equals("},");
    }

    /** determines whether a line signals the end of a feature entry
     * @param line the line **/
    private boolean isEndOfFeature(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("},") && trimmed.endsWith("]");
    }// converts Unix time in seconds (as String from lua field) to Calendar object

    /** determines the Date from a lua assignment with value = unix time in seconds
     * @param line the assignment line
     * @return the corresponding Date object **/
    private Date getDateFromLuaField(String line) {
        long unixTime = Long.parseLong(getLuaFieldValue(line)) * 1000L;
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(unixTime));
        return startTime.getTime();
    }

    /** reads feature objects from file and assigns them to the current feature
     * @param reader the LineNumberReader
     * @param feature the current feature **/
    private void readFeatureObjects(LineNumberReader reader, Feature feature) throws IOException {
        String line = reader.readLine();
        int id = 1;
        while (!isEndOfTable(line)) {
            String term = getEntryFromLuaTable(line);
            feature.addObject(new Feature.FeatureObject(id, term));
            id++;
            line = reader.readLine();
        }
    }
}