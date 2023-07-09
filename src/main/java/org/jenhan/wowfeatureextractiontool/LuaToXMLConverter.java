package org.jenhan.wowfeatureextractiontool;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.Date;
import java.util.logging.Logger;

class LuaToXMLConverter {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private LuaReader luaReader;
    private XMLEventWriter eventWriter;
    private XMLEventFactory eventFactory;
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

    // reads one lua session, writes xml file
    boolean exportToXML(File inputFile, SessionInfo sessionInfo, File outputFile) {
        boolean success = false;
        this.luaReader = new LuaReader(inputFile, sessionInfo.startFeatureTableProperty().get());
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
            // TODO: improve exception handling
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
        Feature nextFeature = luaReader.getNextFeature();
        while (nextFeature != null) {
            writeFeature(nextFeature);
            nextFeature = luaReader.getNextFeature();
        }
        // close open tags
        eventWriter.add(eventFactory.createEndElement("", "", GMAF_DATA));
        eventWriter.add(eventFactory.createEndElement("", "", GMAF_COLLECTION));
    }

    // writes a single feature to the xml file
    private void writeFeature(Feature interaction) throws XMLStreamException {
        log.info("Found next feature");
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
                writeSimpleElement(PROBABILITY, "1", 2); // sets probability always to 1
                writeEndTag(OBJECT, 1);
            }
        }
        //close interaction tag
        writeEndTag(INTERACTION, 0);
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

    private String getEntryFromLuaTable(String line) {
        return luaReader.getEntryFromLuaTable(line);
    }

    private boolean isEndOfTable(String line) {
        return luaReader.isEndOfTable(line);
    }

    private boolean isNextFeature(String line) {
        return luaReader.isNextFeature(line);
    }

    private boolean isEndOfFeature(String line) {
        return luaReader.isEndOfFeature(line);
    }

    private Date getDateFromLuaField(String line) {
        return luaReader.getDateFromLuaField(line);
    }
}
