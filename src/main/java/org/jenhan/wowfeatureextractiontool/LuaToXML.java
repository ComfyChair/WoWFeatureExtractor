package org.jenhan.wowfeatureextractiontool;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
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
    String INTERACTION = "interaction";
    String BEGIN = "begin";
    String TYPE = "type";
    String DESCRIPTION = "description";
    String OBJECT = "object";
    String ID = "id";
    String TERM = "term";
    String PROBABILITY = "probability";
    // Lua table fields
    String SESSION_FIELD_START = "[\"session_";
    String CHAR_NAME = "characterName";
    String SERVER_NAME = "serverName";
    String START_TIME = "startTimeStamp";
    String FEATURE_TABLE = "featureTable";
    String FEATURE_TIMESTAMP = "timestamp";
    String OBJECTS = "objects";


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
        LineNumberReader luaReader = prepareInput(inputFile, sessionInfo.startFeatureTableProperty().get());
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
            // write content
            writeContent(luaReader, eventFactory, xmlEventWriter);
            // close tags
            xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_DATA));
            xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_COLLECTION));
            // write to file and close
            xmlEventWriter.flush();
            xmlEventWriter.close();
            return success;
        } catch (XMLStreamException | IOException e) {
            Gui.errorMessage("Something went wrong while writing the output file");
            log.severe("LuaToXML: Error while writing file");
            e.printStackTrace();
            return false;
        }
    }

    private void writeContent(LineNumberReader luaReader, XMLEventFactory eventFactory, XMLEventWriter xmlEventWriter) throws IOException, XMLStreamException {
        String currentLine = luaReader.readLine();
        log.info("LuaToXML read Line: " + currentLine);
        while (currentLine != null) {
            if (isNextFeature(currentLine)) {
                currentLine = writeFeature(luaReader, eventFactory, xmlEventWriter, currentLine);
            }
            if (isEndOfTable(currentLine)) {
                // close open tags
                xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_DATA));
                xmlEventWriter.add(eventFactory.createEndElement("", "", GMAF_COLLECTION));
                return;
            }
            currentLine = luaReader.readLine();
        }
    }

    private String writeFeature(LineNumberReader luaReader, XMLEventFactory eventFactory, XMLEventWriter xmlEventWriter, String line) throws IOException, XMLStreamException {
        log.info("Found next feature");
        // create feature object and fill with attributes
        Feature interaction = new Feature();
        while (!isEndOfFeature(line)) {
            if (isAssignment(line)) {
                String key = getLuaFieldKey(line);
                switch (key) {
                    case FEATURE_TIMESTAMP -> interaction.setBeginTime(getDateFromLuaField(line));
                    case DESCRIPTION -> interaction.setDescription(getLuaFieldValue(line));
                    case TYPE -> interaction.setType(getLuaFieldValue(line));
                    case OBJECT -> readObjects(luaReader, interaction);
                }
            }
            line = luaReader.readLine();
        }
        // write feature object
        xmlEventWriter.add(eventFactory.createStartElement("", "", INTERACTION));
        xmlEventWriter.add(eventFactory.createAttribute(BEGIN, interaction.getBeginTime().toString()));

        writeSimpleElement(eventFactory, xmlEventWriter, TYPE, interaction.getType().toString());
        writeSimpleElement(eventFactory, xmlEventWriter, DESCRIPTION, interaction.getDescription());
        // iterate over objects
        if (interaction.getObjectList().size() > 0) {
            for (Feature.FeatureObject featureObject : interaction.getObjectList()
            ) {
                xmlEventWriter.add(eventFactory.createStartElement("", "", OBJECT));
                writeSimpleElement(eventFactory, xmlEventWriter, ID, String.valueOf(featureObject.id()));
                writeSimpleElement(eventFactory, xmlEventWriter, TERM, featureObject.term());
                xmlEventWriter.add(eventFactory.createEndElement("", "", OBJECT));
            }
        }
        //close interaction tag
        xmlEventWriter.add(eventFactory.createEndElement("", "", INTERACTION));
        return line;
    }

    private String readObjects(LineNumberReader luaReader, Feature feature) throws IOException {
        String line = luaReader.readLine();
        int id = 1;
        while (!isEndOfTable(line)) {
            String term = getEntryFromLuaTable(line);
            feature.addObject(new Feature.FeatureObject(id, term));
            System.out.println("Found object " + term);
            line = luaReader.readLine();
        }
        return line;
    }

    private static void writeSimpleElement(XMLEventFactory eventFactory, XMLEventWriter xmlEventWriter,
                                           String tag, String content) throws XMLStreamException {
        xmlEventWriter.add(eventFactory.createStartElement("", "", tag));
        xmlEventWriter.add(eventFactory.createCharacters(content));
        xmlEventWriter.add(eventFactory.createEndElement("", "", tag));
    }

    private String getEntryFromLuaTable(String line) {
        return line.trim().split(",")[0].replace("\"", "");
    }

    private boolean isEndOfFeature(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("},") && trimmed.endsWith("]");
    }

    private boolean isEndOfTable(String line) {
        return line.trim().equals("},");
    }

    private boolean isNextFeature(String line) {
        return line.trim().equals("{");
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
                            String fieldKey = LuaToXML.getLuaFieldKey(line);
                            log.fine("is assignment with key: " + fieldKey);
                            switch (fieldKey) {
                                case CHAR_NAME -> charName = LuaToXML.getLuaFieldValue(line);
                                case SERVER_NAME -> serverName = LuaToXML.getLuaFieldValue(line);
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

    // Utility method to convert Unix time in seconds (as in lua field) to Calendar object
    private static Calendar getCalendarFromLuaField(String line) {
        Calendar time;
        long unixTime = Long.parseLong(LuaToXML.getLuaFieldValue(line)) * 1000;
        // convert UNIX time to Calendar
        time = Calendar.getInstance();
        time.setTime(new Date(unixTime));
        return time;
    }

    private static Date getDateFromLuaField(String line) {
        long unixTime = Long.parseLong(LuaToXML.getLuaFieldValue(line)) * 1000;
        return new Date(unixTime);
    }
}
