package org.jenhan.wowfeatureextractiontool;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

class LuaToXMLConverter {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private XMLEventWriter eventWriter;
    private XMLEventFactory eventFactory;
    private LineNumberReader luaReader = null;
    private XMLEvent newLine, tab;
    /** XML Fields **/
    private final static String ENCODING = "utf-8";
    private final static String GMAF_COLLECTION = "gmaf-collection";
    private final static String GMAF_DATA = "gmaf-data";
    private final static String FILE = "file";
    private final static String DATE = "date";
    private final static String INTERACTION = "interaction";
    private final static String BEGIN = "begin";
    private final static String TYPE = "type";
    private final static String DESCRIPTION = "description";
    private final static String OBJECT = "object";
    private final static String ID = "id";
    private final static String TERM = "term";
    private final static String PROBABILITY = "probability";
    /** Lua table fields **/
    private final static String SESSION_FIELD_START = "[\"session_";
    private final static String CHAR_NAME = "characterName";
    private final static String SERVER_NAME = "serverName";
    private final static String START_TIME = "startTimeStamp";
    private final static String FEATURE_TABLE = "featureTable";
    private final static String FEATURE_TIMESTAMP = "timestamp";
    private final static String OBJECT_LIST = "objects";

    /** reader part: prepares input file stream and skips to the selected session **/
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

    /** writer part: prepares output stream **/
    private XMLEventWriter prepareOutput(File outputFile) {
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

    // reads general information about sessions from lua file
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
                            String fieldKey = LuaToXMLConverter.getLuaFieldKey(line);
                            log.fine("is assignment with key: " + fieldKey);
                            switch (fieldKey) {
                                case CHAR_NAME -> charName = LuaToXMLConverter.getLuaFieldValue(line);
                                case SERVER_NAME -> serverName = LuaToXMLConverter.getLuaFieldValue(line);
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

    // reads one lua session, writes xml file
    boolean exportToXML(File inputFile, SessionInfo sessionInfo, File outputFile) {
        boolean success = false;
        this.luaReader = prepareInput(inputFile, sessionInfo.startFeatureTableProperty().get());
        this.eventWriter = prepareOutput(outputFile);
        this.eventFactory = XMLEventFactory.newInstance();
        newLine = eventFactory.createCharacters("\n");
        tab = eventFactory.createCharacters("\t");
        // start writing
        try {
            writeStartOfDocument();
            writeSimpleElement(FILE, outputFile.getName(), 0);
            writeSimpleElement(DATE, sessionInfo.dateProperty().get().toString(), 0);
            // now, write actual content (interaction features)
            writeContent();
            // close tags
            writeEndTag(GMAF_DATA, 0);
            writeEndTag(GMAF_COLLECTION, 0);
            // write to file and close
            eventWriter.flush();
            eventWriter.close();
            return success;
        } catch (XMLStreamException | IOException e) {
            // TODO: improve exception handling?
            if (Gui.getPrimaryStage() != null){
                Gui.errorMessage("Something went wrong while writing the output file");
            }
            log.severe("LuaToXML: Error while writing file");
            try {
                // try to write as much as possible
                eventWriter.flush();
                eventWriter.close();
            } catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            return false;
        }
    }

    private void writeStartOfDocument() throws XMLStreamException {
        // declaration
        eventWriter.add(eventFactory.createStartDocument(ENCODING));
        eventWriter.add(newLine);
        // general information
        eventWriter.add(eventFactory.createStartElement("", "", GMAF_COLLECTION));
        eventWriter.add(newLine);
        eventWriter.add(eventFactory.createStartElement("", "", GMAF_DATA));
        eventWriter.add(newLine);
    }

    // writes the interaction feature part of the xml file
    private void writeContent() throws IOException, XMLStreamException {
        String currentLine = luaReader.readLine();
        log.info("LuaToXML read Line: " + currentLine);
        while (currentLine != null) {
            if (isNextFeature(currentLine)) {
                currentLine = writeFeature(currentLine);
            }
            if (isEndOfTable(currentLine)) {
                // close open tags
                eventWriter.add(eventFactory.createEndElement("", "", GMAF_DATA));
                eventWriter.add(eventFactory.createEndElement("", "", GMAF_COLLECTION));
                return;
            }
            currentLine = luaReader.readLine();
        }
    }

    // writes a single feature to the xml file
    private String writeFeature(String line) throws IOException, XMLStreamException {
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
                    case OBJECT_LIST -> readFeatureObjects(interaction);
                }
            }
            line = luaReader.readLine();
        }
        // start writing interaction feature
        writeStartTagWithAttribute(INTERACTION, 0, BEGIN, interaction.getBeginTime().toString());
        writeSimpleElement(TYPE, interaction.getType().toString(), 1);
        writeSimpleElement(DESCRIPTION, interaction.getDescription(), 1);
        // iterate over contained objects
        if (interaction.getObjectList().size() > 0) {
            for (Feature.FeatureObject featureObject : interaction.getObjectList()
            ) {
                writeSimpleStartTag(OBJECT, 1);
                writeSimpleElement(ID, String.valueOf(featureObject.id()), 2);
                writeSimpleElement(TERM, featureObject.term(), 2);
                writeEndTag(OBJECT, 1);
            }
        }
        //close interaction tag
        writeEndTag(INTERACTION, 0);
        return line;
    }

    // reads feature objects from lua file
    private void readFeatureObjects(Feature feature) throws IOException {
        String line = luaReader.readLine();
        int id = 1;
        while (!isEndOfTable(line)) {
            String term = getEntryFromLuaTable(line);
            feature.addObject(new Feature.FeatureObject(id, term));
            System.out.println("Found object " + term);
            id++;
            line = luaReader.readLine();
        }
    }

    /********************
    /*  Utility methods *
    /********************/

    // writes a simple xml element consisting of start tag, content, and end tag
    private void writeSimpleElement(String name, String content, int indentationLevel) throws XMLStreamException {
        for (int i = 0; i < indentationLevel; i++) {
            eventWriter.add(tab);
        }
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createCharacters(content));
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(newLine);
    }

    private void writeSimpleStartTag(String name, int indentationLevel) throws XMLStreamException {
        for (int i = 0; i < indentationLevel; i++) {
            eventWriter.add(tab);
        }
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(newLine);
    }

    private void writeStartTagWithAttribute(String name, int indentationLevel, String attName, String attValue) throws XMLStreamException {
        for (int i = 0; i < indentationLevel; i++) {
            eventWriter.add(tab);
        }
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createAttribute(attName, attValue));
        eventWriter.add(newLine);
    }

    private void writeEndTag(String name, int indentationLevel) throws XMLStreamException {
        for (int i = 0; i < indentationLevel; i++) {
            eventWriter.add(tab);
        }
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(newLine);
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

    private String getEntryFromLuaTable(String line) {
        String trimmed = line.trim();
        String leftOfComma = trimmed.split(",")[0];
        return leftOfComma.replace("\"", "");
    }

    private boolean isEndOfTable(String line) {
        return line.trim().equals("},");
    }

    private boolean isNextFeature(String line) {
        return line.trim().equals("{");
    }

    private boolean isEndOfFeature(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("},") && trimmed.endsWith("]");
    }

    // converts Unix time in seconds (as String from lua field) to Calendar object
    private static Calendar getCalendarFromLuaField(String line) {
        Calendar time;
        long unixTime = Long.parseLong(LuaToXMLConverter.getLuaFieldValue(line)) * 1000;
        // convert UNIX time to Calendar
        time = Calendar.getInstance();
        time.setTime(new Date(unixTime));
        return time;
    }

    private Date getDateFromLuaField(String line) {
        long unixTime = Long.parseLong(LuaToXMLConverter.getLuaFieldValue(line)) * 1000;
        return new Date(unixTime);
    }
}
