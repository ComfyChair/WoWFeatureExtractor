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
                    log.finer("found session, line number: " + startLine);
                    String charName = null;
                    String serverName = null;
                    Calendar startTime = null;
                    // read session lines until all info fields are populated
                    while ((charName == null) || (serverName == null) || (startTime == null)) {
                        line = luaReader.readLine();
                        log.finer("Read line: " + line);
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
                    SessionInfo newSessionInfo = new SessionInfo(sessionID, startLine, charName, serverName, startTime);
                    sessionList.add(newSessionInfo);
                    log.fine("Added session: " + newSessionInfo);
                }
            }
        } catch (IOException e) {
            //TODO: handle IOException
            throw new RuntimeException(e);
        }
        return sessionList;

    }

    private static Calendar getTimeFromLuaField(String line) {
        Calendar time;
        long unixTime = Long.parseLong(LuaToXML.getLuaFieldValue(line)) * 1000;
        // convert UNIX time to Calendar
        time = Calendar.getInstance();
        time.setTime(new Date(unixTime));
        return time;
    }

    @Override
    public boolean exportToXML(File inputFile, File outputFile) {
        boolean success = false;
        LineNumberReader luaReader = LuaToXML.getReader(inputFile);
        PrintWriter xmlWriter = LuaToXML.getWriter(inputFile, outputFile);
        try {
            writeDate(xmlWriter, sessionInfo.time);
            skipToStartLine(luaReader);
            skipToFeatureTable(luaReader);
            // now start with the actual data
            String currentLine;
            while ((currentLine = luaReader.readLine()) != null // security check for end of file
                    && !currentLine.trim().equals("},")){ // end of FeatureTable
                log.info("Current line: " + currentLine);
                if (currentLine.trim().equals("{")){
                    // new interaction feature
                    log.info("   New feature!");
                    Feature thisFeature = new Feature();
                    currentLine = luaReader.readLine();
                    log.info("Current line: " + currentLine);
                    while (LuaToXML.isAssignment(currentLine)){
                        log.info("   Assignment found at line " + luaReader.getLineNumber());
                        switch (LuaToXML.getLuaFieldKey(currentLine)){
                            case "timestamp" -> thisFeature.setCalendar(getTimeFromLuaField(currentLine));
                            case "description" -> thisFeature.setDescription(LuaToXML.getLuaFieldValue(currentLine));
                            case "objects" -> addObjects(luaReader, thisFeature);
                            default -> log.fine("this must be the startTimeStamp tag");
                        }
                        currentLine = luaReader.readLine();
                    }
                    writeInteractionTag(xmlWriter, thisFeature);
                }
            }
            //close tags
            xmlWriter.write(GMAF_DATA.getCloseTag());
            xmlWriter.write(GMAF_COLLECTION.getCloseTag());
            //flush and close at the end
            xmlWriter.flush();
            xmlWriter.close();
            return success;
        } catch (IOException e) {
            //TODO: handle exceptions here
            log.severe("Error while converting");
            return false;
        }
    }

    private void addObjects(LineNumberReader luaReader, Feature thisfeature) throws IOException {
        log.info("   Adding objects to feature");
        int id = 1;
        String currentLine;
        while ((currentLine = luaReader.readLine()) != null // security check
                && !currentLine.trim().equals("},")){ // closing brackets of objects table
            log.info("Current line: " + currentLine);
            String[] split = currentLine.split("\",");
            // first part of split is object term, omit leading quotation mark
            String term = split[0].trim().substring(1);
            log.info("Term: " + term);
            if (!term.equals("")){
                Feature.FeatureObject newObject = new Feature.FeatureObject(id, term);
                thisfeature.addObject(newObject);
                id++;
            }
        }
    }

    private void writeSimpleTag(PrintWriter xmlWriter, XmlTag tagType, int tabLength, String content) {
        log.info("Writing " + tagType + " tag");
        for (int i = 0; i < tabLength; i++) {
            xmlWriter.write("\t");
        }
        xmlWriter.write(tagType.getOpenTag());
        xmlWriter.write(content);
        xmlWriter.write(tagType.getCloseTag());
    }

    private void writeDate(PrintWriter xmlWriter, Calendar time) {
        log.info("Writing date");
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String recordingDate = simpleDateFormat.format(time.getTime());
        xmlWriter.write(GMAF_DATE.getOpenTag() + recordingDate + GMAF_DATE.getCloseTag());
    }

    private void writeInteractionTag(PrintWriter xmlWriter, Feature thisFeature) {
        log.info("Writing interaction tag");
        // get human-readable time format
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String recordingTime = simpleDateFormat.format(thisFeature.getCalendar().getTime());
        // start writing
        xmlWriter.write("<interaction begin=" + "'" + recordingTime + "'>\n");
        //writeSimpleTag(xmlWriter,  GMAF_TYPE, 1, thisFeature.getType().name());
        writeSimpleTag(xmlWriter, GMAF_DESCRIPTION, 1, thisFeature.getDescription());
        for (Feature.FeatureObject object: thisFeature.getObjectList()
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

    private static void skipToFeatureTable(LineNumberReader luaReader) throws IOException {
        boolean found = false;
        log.info("Skipping to feature table");
        String nextLine;
        while (!found && (nextLine = luaReader.readLine()) != null){
            log.info("Current line: " + nextLine);
            if (LuaToXML.isAssignment(nextLine) && LuaToXML.getLuaFieldKey(nextLine).equals(FEATURE_TABLE)){
                int lineNumber = luaReader.getLineNumber();
                log.info("Found feature table at line " + lineNumber);
                found = true;
            }
        }
    }

    private void skipToStartLine(LineNumberReader luaReader) throws IOException {
        log.info("Skipping to start line");
        for (int i = 0; i < this.sessionInfo.startLine; i++) {
            luaReader.readLine();
        }
    }

}
