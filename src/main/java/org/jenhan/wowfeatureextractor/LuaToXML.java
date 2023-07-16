package org.jenhan.wowfeatureextractor;

import java.io.File;

/** interface for conversion from lua session data to gmaf xml **/
public interface LuaToXML {
    /** XML element constant **/
    String DECLARATION = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>";
    /** XML element constant **/
    String GMAF_COLLECTION = "gmaf-collection";
    /** XML element constant **/
    String GMAF_DATA = "gmaf-data";
    /** XML element constant **/
    String FILE = "file";
    /** XML element constant **/
    String DATE = "date";
    /** XML element constant **/
    String INTERACTION = "interaction";
    /** XML element constant **/
    String BEGIN = "begin";
    /** XML element constant **/
    String TYPE = "type";
    /** XML element constant **/
    String DESCRIPTION = "description";
    /** XML element constant **/
    String OBJECT = "object";
    /** XML element constant **/
    String ID = "id";
    /** XML element constant **/
    String TERM = "term";
    /** XML element constant **/
    String PROBABILITY = "probability";

    /** exports recorded interaction features to gmaf-compatible xml
     * @param outputFile the output file
     * @return returns true on success, false otherwise **/
    boolean exportToXML(File outputFile);
}
