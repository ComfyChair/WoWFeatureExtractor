package org.jenhan.wowfeatureextractiontool;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public interface LuaToXML {
    Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    // XML Fields
    String ENCODING = "utf-8";
    String GMAF_COLLECTION = "gmaf-collection";
    String GMAF_DATA = "gmaf-data";
    String FILE = "file";
    String DATE = "date";
    String GMAF_TYPE = "type";
    String GMAF_DESCRIPTION = "description";
    String GMAF_OBJECT = "object";
    String GMAF_ID = "id";
    String GMAF_TERM = "term";
    String GMAF_PROBABILITY = "probability";
    // Lua table fields
    String SESSION_FIELD_START = "[\"session_";
    String CHAR_NAME = "characterName";
    String SERVER_NAME = "serverName";
    String START_TIME = "startTimeStamp";
    String FEATURE_TABLE = "featureTable";


    // Utility methods
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

    // reader part: prepares input file stream and skips to the selected session
    private static LineNumberReader prepareInput(File inputFile, int startLine) {
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
    }

    // writer part: prepares output stream
    private static XMLEventWriter prepareOutput(File outputFile) {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter xmlWriter = null;
        try {
            xmlWriter = xmlOutputFactory.createXMLEventWriter(new BufferedWriter(new FileWriter(outputFile)));
        } catch (IOException | XMLStreamException e) {
            Gui.errorMessage("Something went wrong while preparing the output file");
            log.severe("Error while preparing output stream");
            e.printStackTrace();
        }
        return xmlWriter;
    }

    // main interface function
    // reads lua session, writes xml file
    // returns true upon success, false upon failure
    default boolean exportToXML(File inputFile, SessionInfo sessionInfo, File outputFile) {
        boolean success = false;
        LineNumberReader luaReader = prepareInput(inputFile, sessionInfo.startLineProperty().get());
        XMLEventWriter xmlEventWriter = prepareOutput(outputFile);
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        // start writing
        try {
            xmlEventWriter.add(eventFactory.createStartDocument(ENCODING));
            // start writing content
            xmlEventWriter.add(eventFactory.createStartElement("", "", GMAF_COLLECTION));
            xmlEventWriter.add(eventFactory.createStartElement("", "", GMAF_DATA));
            // write file name
            xmlEventWriter.add(eventFactory.createStartElement("", "", FILE));
            xmlEventWriter.add(eventFactory.createCharacters(outputFile.getName()));
            xmlEventWriter.add(eventFactory.createEndElement("", "", FILE));
            // write date
            xmlEventWriter.add(eventFactory.createStartElement("", "", DATE));
            xmlEventWriter.add(eventFactory.createCharacters(sessionInfo.dateProperty().get().toString()));
            xmlEventWriter.add(eventFactory.createEndElement("", "", DATE));
            // start writing interaction features
            // close tags
            xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_DATA));
            xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_COLLECTION));
            // write to file and close
            xmlEventWriter.flush();
            xmlEventWriter.close();
            return success;
        } catch (XMLStreamException e) {
            Gui.errorMessage("Something went wrong while writing the output file");
            log.severe("XMLStream: Error while writing file");
            e.printStackTrace();
            return false;
        }
    }

    static List<SessionInfo> readSessionInfo(File luaFile) {
        LineNumberReader luaReader = prepareInput(luaFile, 0);
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
                    int startLine = luaReader.getLineNumber();
                    log.fine("found session, line number: " + startLine + ", content: " + startLine);
                    String charName = null;
                    String serverName = null;
                    Calendar startTime = null;
                    // read session lines until all info fields are populated
                    while ((charName == null) || (serverName == null) || (startTime == null)) {
                        line = luaReader.readLine();
                        log.fine("Read line: " + line);
                        if (isAssignment(line)) {
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
}
