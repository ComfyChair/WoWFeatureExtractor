package org.jenhan.wowfeatureextractiontool;

import javafx.scene.control.Alert;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.logging.Logger;

public class LuaReader {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /**
     * Lua table fields
     **/
    private final static String SESSION_FIELD_START = "[\"session_";
    private final static String CHAR_NAME = "characterName";
    private final static String SERVER_NAME = "serverName";
    private final static String START_TIME = "startTimeStamp";
    private final static String FEATURE_TABLE = "featureTable";
    private final static String DESCRIPTION = "description";
    private final static String TYPE = "type";
    private final static String FEATURE_TIMESTAMP = "timestamp";
    private final static String OBJECT_LIST = "objects";
    private final static String END_OF_TABLE_ENTRY = "},";
    private final static String END_OF_SESSION  = "\t},";
    private final static String END_OF_FEATURE_TABLE = "\t\t},";
    private final static String END_OF_TABLE ="}";

    public LuaReader() {
    }

    List<Session> readFile(File luaFile) {
        List<Session> sessionList = new ArrayList<>();
        String line;
        int sessionID = -1;
        try (LineNumberReader reader = new LineNumberReader(new FileReader(luaFile))){
            while ((!Objects.equals(line = reader.readLine(), END_OF_TABLE))) { // null marks the end of the stream
                log.fine("Reading a line of " + luaFile);
                line = line.trim();
                if (line.startsWith(SESSION_FIELD_START)) {
                    sessionID++;
                    Session newSession = Session.create(sessionID, luaFile.getName());
                    log.fine("found session, line number: " + reader.getLineNumber() + ", content of last read line: " + line);
                    // read session lines until all info fields are populated
                    readSingleSession(reader, line, newSession);
                    // set fields and add session info to List
                    sessionList.add(newSession);
                    log.fine("Added session: " + newSession);
                }
            }
        } catch (IOException e) {
            //TODO: handle properly
            log.severe("IOException while reading session info of file: " + luaFile
                    + "\n" + e);
            if (Gui.getPrimaryStage() != null) { // check to prevent errors when testing without gui
                Gui.feedbackDialog(Alert.AlertType.ERROR, "Could not extract session information", "");
            }
        }
        return sessionList;
    }

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


    boolean isNextFeature(String line) {
        return line.trim().equals("{");
    }

    private Feature readFeature(LineNumberReader reader) throws IOException {
        // create feature object and fill with attributes
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

    static boolean isAssignment(String line) {
        String[] split = line.split("=");
        return split.length > 1;
    }

    static String getLuaFieldValue(String line) {
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
            return valueSide;
        } else {
            throw new RuntimeException("Requested Lua field value on non-assignment line");
        }
    }

    static String getLuaFieldKey(String line) {
        if (isAssignment(line)) {
            String[] split = line.split("=");
            // left hand side of line = key
            String keySide = split[0].trim();
            // substring to omit quotation marks and brackets
            return keySide.substring(2, keySide.length() - 2);
        } else {
            throw new InvalidParameterException("Requested Lua field key on non-assignment line");
        }
    }

    String getEntryFromLuaTable(String line) {
        String trimmed = line.trim();
        String leftOfComma = trimmed.split(",")[0];
        return leftOfComma.replace("\"", "");
    }

    boolean isEndOfTable(String line) {
        return line.trim().equals("},");
    }

    boolean isEndOfFeature(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("},") && trimmed.endsWith("]");
    }// converts Unix time in seconds (as String from lua field) to Calendar object

    Date getDateFromLuaField(String line) {
        long unixTime = Long.parseLong(getLuaFieldValue(line)) * 1000L;
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(unixTime));
        return startTime.getTime();
    }


    // reads feature objects from lua file
    private void readFeatureObjects(LineNumberReader reader, Feature feature) throws IOException {
        String line = reader.readLine();
        int id = 1;
        while (!isEndOfTable(line)) {
            String term = getEntryFromLuaTable(line);
            feature.addObject(new Feature.FeatureObject(id, term, 1));
            id++;
            line = reader.readLine();
        }
    }
}