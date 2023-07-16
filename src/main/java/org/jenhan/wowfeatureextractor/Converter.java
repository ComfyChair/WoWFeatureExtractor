package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;

/** Converter class for conversion of interaction feature data to xml **/
class Converter implements LuaToXML {
    /** outmost element for xml output **/
    private final Collection collection;

    /** @param session the recording session the converter gets bound to **/
    public Converter(Session session) {
        this.collection = new Collection();
        collection.setSession(session);
    }

    @Override
    public boolean exportToXML(File outputFile) {
        System.out.println("Output file: " + outputFile);
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
    static class Collection {
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
