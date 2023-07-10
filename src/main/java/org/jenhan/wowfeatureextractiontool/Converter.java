package org.jenhan.wowfeatureextractiontool;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
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
