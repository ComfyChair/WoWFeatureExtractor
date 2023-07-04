package org.jenhan;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.logging.Logger;

public interface LuaToXML {
    Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
    XmlTag GMAF_COLLECTION = new XmlTag("gmaf-collection");
    XmlTag GMAF_DATA = new XmlTag("gmaf-data");
    XmlTag GMAF_FILE = new XmlTag("file");
    XmlTag GMAF_DATE = new XmlTag("date");
    XmlTag GMAF_TYPE = new XmlTag("type");
    XmlTag GMAF_DESCRIPTION = new XmlTag("description");
    XmlTag GMAF_OBJECT = new XmlTag("object");
    XmlTag GMAF_ID = new XmlTag("id");
    XmlTag GMAF_TERM = new XmlTag("term");
    XmlTag GMAF_PROBABILITY = new XmlTag("probability");

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

    // direct file access functions with exception handling
    static LineNumberReader getReader(File luaFile) {
        return prepareInput(luaFile);
    }

    static PrintWriter getWriter(File luaFile, File xmlFile) {
        return prepareOutput(luaFile, xmlFile);
    }

    private static LineNumberReader prepareInput(File inputFile) {
        LineNumberReader luaReader = null;
        try {
            luaReader = new LineNumberReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            // TODO: handle input exception with user feedback dialog
            log.severe("Could not read file");
        }
        return luaReader;
    }

    private static PrintWriter prepareOutput(File inputFile, File outputFile) {
        PrintWriter xmlWriter = null;
        try {
            xmlWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
            // first five lines can be already written
            xmlWriter.write(XML_HEADER);
            xmlWriter.write(GMAF_COLLECTION.getOpenTag() + "\n\n");
            xmlWriter.write(GMAF_DATA.getOpenTag() + "\n");
            xmlWriter.write(GMAF_FILE.getOpenTag() + inputFile.getName() + GMAF_FILE.getCloseTag());

        } catch (IOException e) {
            // TODO: handle output exception with user feedback dialog
            log.severe("output error");
        }
        return xmlWriter;
    }

    // main interface function
    // reads lua session, writes xml file
    // returns true upon success, false upon failure
    boolean exportToXML(File inputFile, File outputFile);

    final class XmlTag {
        String tagName;
        String openTag;
        String closeTag;

        public XmlTag(String tagName) {
            this.tagName = tagName;
            this.openTag = "<" + tagName + ">";
            this.closeTag = "</" + tagName + ">\n";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (XmlTag) obj;
            return Objects.equals(this.tagName, that.tagName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tagName);
        }

        @Override
        public String toString() {
            return "XmlTag<" + tagName + '>';
        }

        public String getOpenTag() {
            return openTag;
        }

        public String getCloseTag() {
            return closeTag;
        }
    }


}
