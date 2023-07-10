package org.jenhan.wowfeatureextractiontool;

import java.io.File;

public interface LuaToXML {
    /**
     * XML Fields
     **/
    String DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>";
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

    boolean exportToXML(File outputFile);
}
