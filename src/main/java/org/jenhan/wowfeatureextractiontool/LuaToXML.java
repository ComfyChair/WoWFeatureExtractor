package org.jenhan.wowfeatureextractiontool;

import java.io.File;

/** interface for conversion from lua session data to gmaf xml **/
public interface LuaToXML {
    /** XML element name **/
    String DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>";
    /** XML element name **/
    String GMAF_COLLECTION = "gmaf-collection";
    /** XML element name **/
    String GMAF_DATA = "gmaf-data";
    /** XML element name **/
    String FILE = "file";
    /** XML element name **/
    String DATE = "date";
    /** XML element name **/
    String INTERACTION = "interaction";
    /** XML element name **/
    String BEGIN = "begin";
    /** XML element name **/
    String TYPE = "type";
    /** XML element name **/
    String DESCRIPTION = "description";
    /** XML element name **/
    String OBJECT = "object";
    /** XML element name **/
    String ID = "id";
    /** XML element name **/
    String TERM = "term";
    /** XML element name **/
    String PROBABILITY = "probability";

    /** exports recorded interaction features to gmaf-compatible xml
     * @param outputFile the output file
     * @return returns true on success, false otherwise **/
    boolean exportToXML(File outputFile);
}
