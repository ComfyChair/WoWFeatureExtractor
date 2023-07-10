package org.jenhan.wowfeatureextractiontool;

import java.io.File;

public interface LuaToXML {
    /**
     * XML Fields
     **/
    final static String DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>";
    final static String GMAF_COLLECTION = "gmaf-collection";
    final static String GMAF_DATA = "gmaf-data";
    final static String FILE = "file";
    final static String DATE = "date";
    final static String INTERACTION = "interaction";
    final static String BEGIN = "begin";
    final static String TYPE = "type";
    final static String DESCRIPTION = "description";
    final static String OBJECT = "object";
    final static String ID = "id";
    final static String TERM = "term";
    final static String PROBABILITY = "probability";

    boolean exportToXML(File outputFile);
}
