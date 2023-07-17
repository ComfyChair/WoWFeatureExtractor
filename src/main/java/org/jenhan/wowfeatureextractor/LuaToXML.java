package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
     * @param session session object that is going to be to be converted
     * @param outputFile the output file
     * @return returns true on success, false otherwise **/
    default boolean exportToXML(Session session, File outputFile){
        System.out.println("Output file: " + outputFile);
        Collection collection = new Collection();
        collection.setSession(session);
        boolean success = false;
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(Collection.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(collection, outputFile);
            success = true;
        } catch (JAXBException e) {
            MainControl.handleError("Error while exporting session data to XML\n", e);
        }
        return success;
    }

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
