package org.jenhan.wowfeatureextractiontool;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.util.logging.Logger;

class Converter implements LuaToXML{
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Collection collection;

    public Converter(Session session) {
        this.collection = new Collection(session);
    }

    @Override
    public boolean exportToXML(File outputFile) {
        JAXBContext context = null;
        boolean success = false;
        try {
            context = JAXBContext.newInstance(Collection.class);
            Marshaller mar= context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(collection, outputFile);
        } catch (JAXBException e) {
            //TODO: handle properly
            throw new RuntimeException(e);
        }
        return success;
    }

    @XmlRootElement(name = LuaToXML.GMAF_COLLECTION)
    static class Collection{
        private Session session;

        public Collection(Session session) {
            this.session = session;
        }

        @XmlElement(name = LuaToXML.GMAF_DATA)
        Session getSession(){
            return session;
        }

    }
}
