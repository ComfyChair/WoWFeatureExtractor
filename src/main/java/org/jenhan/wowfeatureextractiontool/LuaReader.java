package org.jenhan.wowfeatureextractiontool;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class LuaReader {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private LineNumberReader lineNumberReader = null;
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

    LuaReader(File luaFile, int startLine) {
        lineNumberReader = prepareInput(luaFile, startLine);
    }

    /**
     * reader part: prepares input file stream and skips to the selected session
     **/
    static private LineNumberReader prepareInput(File inputFile, int startLine) {
        LineNumberReader luaReader = null;
        try {
            luaReader = new LineNumberReader(new FileReader(inputFile));
            for (int i = 0; i < startLine; i++) {
                luaReader.readLine();
            }
        } catch (FileNotFoundException e) {
            Gui.errorMessage("Something went wrong while preparing the input file : " + inputFile.getAbsolutePath());
            log.severe("Could not read input file: " + inputFile.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            Gui.errorMessage("Something went wrong while reading the input file : " + inputFile.getAbsolutePath());
            log.severe("Error while skipping in input file: " + inputFile.getAbsolutePath());
            e.printStackTrace();
        }
        return luaReader;
    }// reads general information about sessions from lua file

    static List<SessionInfo> readSessionInfo(File luaFile) {
        LineNumberReader luaReader = prepareInput(luaFile, 0);
        List<SessionInfo> sessionList = new ArrayList<SessionInfo>();
        String line;
        int sessionID = -1;
        try {
            while (((line = luaReader.readLine()) != null)) { // null marks the end of the stream
                log.fine("Reading a line of" + luaFile);
                line = line.trim();
                if (line.startsWith(SESSION_FIELD_START)) {
                    sessionID++;
                    int startLine = luaReader.getLineNumber();
                    SessionInfo newSessionInfo = new SessionInfo(sessionID, startLine);
                    log.info("found session, line number: " + startLine + ", content of last read line: " + line);
                    String charName = null;
                    String serverName = null;
                    Calendar startTime = null;
                    Integer startFeatureTable = null;
                    // read session lines until all info fields are populated
                    while ((charName == null) || (serverName == null) ||
                            (startTime == null) || (startFeatureTable == null)) {
                        line = luaReader.readLine();
                        log.fine("Read line: " + line);
                        if (isAssignment(line)) {
                            String fieldKey = getLuaFieldKey(line);
                            log.fine("is assignment with key: " + fieldKey);
                            switch (fieldKey) {
                                case CHAR_NAME -> charName = getLuaFieldValue(line);
                                case SERVER_NAME -> serverName = getLuaFieldValue(line);
                                case START_TIME -> startTime = getCalendarFromLuaField(line);
                                case FEATURE_TABLE -> startFeatureTable = luaReader.getLineNumber();
                                default -> log.finer("line not relevant");
                            }
                        }
                    }
                    // set fields and add session info to List
                    newSessionInfo.setContentProperties(charName, serverName, startTime, startFeatureTable);
                    sessionList.add(newSessionInfo);
                    log.fine("Added session: " + newSessionInfo);
                }
            }
        } catch (IOException e) {
            log.severe("IOException while reading session info of file: " + luaFile
                    + "\n" + e);
            if (Gui.getPrimaryStage() != null) { // check to prevent errors when testing without gui
                Gui.errorMessage("Could not extract session information");
            }
        }
        return sessionList;
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
            throw new InvalidParameterException("something went wrong while manipulating lua file strings");
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
            throw new InvalidParameterException("something went wrong while manipulating lua file strings");
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

    boolean isNextFeature(String line) {
        return line.trim().equals("{");
    }

    boolean isEndOfFeature(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("},") && trimmed.endsWith("]");
    }// converts Unix time in seconds (as String from lua field) to Calendar object

    static Calendar getCalendarFromLuaField(String line) {
        Calendar time;
        long unixTime = Long.parseLong(getLuaFieldValue(line)) * 1000;
        // convert UNIX time to Calendar
        time = Calendar.getInstance();
        time.setTime(new Date(unixTime));
        return time;
    }

    Date getDateFromLuaField(String line) {
        long unixTime = Long.parseLong(getLuaFieldValue(line)) * 1000;
        return new Date(unixTime);
    }

    Feature getNextFeature() throws IOException {
        String currentLine = lineNumberReader.readLine();
        log.info("GetNext Feature - Current line: " + currentLine);
        Feature returnFeature = null;
        while (currentLine != null) {
            log.fine("LuaReader read Line: " + currentLine);
            if (isNextFeature(currentLine)) {
                returnFeature = readFeature(currentLine);
            }
            currentLine = lineNumberReader.readLine();
        }
        return returnFeature;
    }

    private Feature readFeature(String line) throws IOException {
        // create feature object and fill with attributes
        Feature interaction = new Feature();
        while (!isEndOfFeature(line)) {
            if (isAssignment(line)) {
                String key = getLuaFieldKey(line);
                switch (key) {
                    case FEATURE_TIMESTAMP -> interaction.setBeginTime(getDateFromLuaField(line));
                    case DESCRIPTION -> interaction.setDescription(LuaReader.getLuaFieldValue(line));
                    case TYPE -> interaction.setType(LuaReader.getLuaFieldValue(line));
                    case LuaReader.OBJECT_LIST -> readFeatureObjects(interaction);
                }
            }
            line = lineNumberReader.readLine();
        }
        return interaction;
    }

    // reads feature objects from lua file

    private void readFeatureObjects(Feature feature) throws IOException {
        String line = lineNumberReader.readLine();
        int id = 1;
        while (!isEndOfTable(line)) {
            String term = getEntryFromLuaTable(line);
            feature.addObject(new Feature.FeatureObject(id, term));
            id++;
            line = lineNumberReader.readLine();
        }
    }
}