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
    /* constants */
    private static final String SESSION_FIELD_START = "[\"session_";
    private static final String CHAR_NAME = "characterName";
    private static final String SERVER_NAME = "serverName";
    private static final String START_TIME = "startTimeStamp";
    private static final String FEATURE_TABLE = "featureTable";
    // session information record
    private final SessionInfo sessionInfo;

    // constructor
    Session(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    // reads session info from a recorded .lua file, so that it can be shown to the user in a session selection dialog
    static List<SessionInfo> readSessionInfo(File luaFile) {
        LineNumberReader luaReader = LuaToXML.getReader(luaFile);
        log.fine("Got reader for file " + luaFile);
        List<SessionInfo> sessionList = new ArrayList<>();
        String line;
        int sessionID = -1;
        try {
            while (((line = luaReader.readLine()) != null)) { // null marks the end of the stream
                log.fine("Reading a line of" + luaFile);
                line = line.trim();
                if (line.startsWith(SESSION_FIELD_START)) {
                    log.fine("Found session start line" + luaFile);
                    sessionID++;
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
                                case START_TIME -> startTime = getTimeFromLuaField(line);
                                default -> log.finer("line not relevant");
                            }
                        }
                    }
                    // compile information into SessionInfo record and add to List
                    SessionInfo newSessionInfo = new SessionInfo(sessionID, startLine, charName, serverName, startTime);
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

    // Utility method to convert Unix time in seconds (as in lua field) to Calendar object
    private static Calendar getTimeFromLuaField(String line) {
        Calendar time;
        long unixTime = Long.parseLong(LuaToXML.getLuaFieldValue(line)) * 1000;
        // convert UNIX time to Calendar
        time = Calendar.getInstance();
        time.setTime(new Date(unixTime));
        return time;
    }

    //TODO: make skipping more robust: just scan for beginning of a line
    private static void skipToFeatureTable(LineNumberReader luaReader) throws IOException {
        boolean found = false;
        log.fine("Skipping to feature table");
        String nextLine;
        while (!found && (nextLine = luaReader.readLine()) != null) {
            log.fine("Current line: " + nextLine);
            if (LuaToXML.isAssignment(nextLine) && LuaToXML.getLuaFieldKey(nextLine).equals(FEATURE_TABLE)) {
                int lineNumber = luaReader.getLineNumber();
                log.fine("Found feature table at line " + lineNumber);
                found = true;
            }
        }
    }

    // returns info for a single session
    SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    // converts lua feature table to GMAF-style xml
    @Override
    public boolean exportToXML(File inputFile, File outputFile) {
        boolean success = false;
        LineNumberReader luaReader = LuaToXML.getReader(inputFile);
        PrintWriter xmlWriter = LuaToXML.getWriter(inputFile, outputFile);
        try {
            writeDate(xmlWriter, sessionInfo.timeProperty().get().toString());
            skipToStartLine(luaReader);
            skipToFeatureTable(luaReader);
            // now start with the actual data
            String currentLine;
            while ((currentLine = luaReader.readLine()) != null // security check for end of file
                    && !currentLine.trim().equals("},")) { // end of FeatureTable
                log.fine("Current line: " + currentLine);
                if (currentLine.trim().equals("{")) {
                    // new interaction feature
                    log.fine("   New feature");
                    Feature thisFeature = new Feature();
                    currentLine = luaReader.readLine();
                    log.fine("Current line: " + currentLine);
                    while (LuaToXML.isAssignment(currentLine)) {
                        log.fine("   Assignment found at line " + luaReader.getLineNumber());
                        switch (LuaToXML.getLuaFieldKey(currentLine)) {
                            case "timestamp" -> thisFeature.setCalendar(getTimeFromLuaField(currentLine));
                            case "description" -> thisFeature.setDescription(LuaToXML.getLuaFieldValue(currentLine));
                            case "type" -> thisFeature.setType(LuaToXML.getLuaFieldValue(currentLine));
                            case "objects" -> addObjects(luaReader, thisFeature);
                            default -> log.fine("this must be the startTimeStamp tag");
                        }
                        currentLine = luaReader.readLine();
                    }
                    writeInteractionTag(xmlWriter, thisFeature);
                }
            }
            // close tags
            xmlWriter.write(GMAF_DATA.getCloseTag());
            xmlWriter.write(GMAF_COLLECTION.getCloseTag());
            // flush and close files at the end
            xmlWriter.flush();
            xmlWriter.close();
            luaReader.close();
            log.fine("Closed input and output files");
            return success;
        } catch (IOException e) {
            String message = "Error while converting";
            log.severe(message + "\n" + e);
            if (Gui.getPrimaryStage() != null) { // check to prevent errors when testing without gui
                Gui.errorMessage("Error while converting " + inputFile + " to " + outputFile);
            }
            return false;
        }
    }

    // adds objects to features
    private void addObjects(LineNumberReader luaReader, Feature thisfeature) throws IOException {
        log.fine("   Adding objects to feature");
        int id = 1;
        String currentLine;
        while ((currentLine = luaReader.readLine()) != null // security check
                && !currentLine.trim().equals("},")) { // closing brackets of objects table
            log.fine("Current line: " + currentLine);
            String[] split = currentLine.split("\",");
            // first part of split is object term, omit leading quotation mark
            String term = split[0].trim().substring(1);
            log.fine("Term: " + term);
            if (!term.equals("")) {
                Feature.FeatureObject newObject = new Feature.FeatureObject(id, term);
                thisfeature.addObject(newObject);
                id++;
            }
        }
    }

    //TODO: switch xml file creation to java library to make any changes more easily manageable
    private void writeSimpleTag(PrintWriter xmlWriter, XmlTag tagType, int tabLength, String content) {
        log.fine("Writing " + tagType + " tag");
        for (int i = 0; i < tabLength; i++) {
            xmlWriter.write("\t");
        }
        xmlWriter.write(tagType.getOpenTag());
        xmlWriter.write(content);
        xmlWriter.write(tagType.getCloseTag());
    }

    private void writeDate(PrintWriter xmlWriter, String dateString) {
        log.fine("Writing date");
        xmlWriter.write(GMAF_DATE.getOpenTag() + dateString + GMAF_DATE.getCloseTag());
    }

    // writes the current interaction feature to the xml file
    private void writeInteractionTag(PrintWriter xmlWriter, Feature thisFeature) {
        log.fine("Writing interaction tag");
        // get human-readable time format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String recordingTime = simpleDateFormat.format(thisFeature.getCalendar().getTime());
        // start writing
        xmlWriter.write("<interaction begin=" + "'" + recordingTime + "'>\n");
        writeSimpleTag(xmlWriter, GMAF_TYPE, 1, thisFeature.getType().name());
        writeSimpleTag(xmlWriter, GMAF_DESCRIPTION, 1, thisFeature.getDescription());
        for (Feature.FeatureObject object : thisFeature.getObjectList()
        ) {
            xmlWriter.write("\t" + GMAF_OBJECT.getOpenTag() + "\n");
            writeSimpleTag(xmlWriter, GMAF_ID, 2, String.valueOf(object.id()));
            writeSimpleTag(xmlWriter, GMAF_TERM, 2, object.term());
            writeSimpleTag(xmlWriter, GMAF_PROBABILITY, 2, "1.00");
            xmlWriter.write("\t" + GMAF_OBJECT.getCloseTag());
        }
        // close interaction feature tag
        xmlWriter.write("</interaction>\n");
    }

    private void skipToStartLine(LineNumberReader luaReader) throws IOException {
        log.fine("Skipping to start line");
        for (int i = 0; i < this.sessionInfo.startLineProperty().get(); i++) {
            luaReader.readLine();
        }
    }

}
