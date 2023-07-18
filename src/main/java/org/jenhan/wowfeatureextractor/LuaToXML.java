package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;

/** interface for conversion from lua session data to gmaf xml **/
public interface LuaToXML {
    /** XML element constants **/
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

    /** exports recorded interaction features to gmaf-compatible xml
     * @param outputFile the output file
     * @return returns true on success, false otherwise **/
    boolean exportToXML(File outputFile);

    /** outmost element in output xml **/
    @XmlRootElement(name = LuaToXML.GMAF_COLLECTION)
    class Collection {
        /** the session object the collection is created from **/
        private Session session;

        /** explicit standard constructor necessary for marshalling to xml **/
        Collection() {
        }

        /** @param session the session object the collection is created from; called on creation **/
        protected void setSession(Session session) {
            this.session = session;
        }

        /** @return session gets marshalled to gmaf-data element **/
        @XmlElement(name = LuaToXML.GMAF_DATA)
        Session getSession() {
            return session;
        }
    }
}
