package org.jenhan.wowfeatureextractiontool;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.logging.Logger;

class Converter implements LuaToXML {
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Collection collection;

    public Converter(Session session) {
        this.collection = new Collection();
        collection.setSession(session);
    }

    @Override
    public boolean exportToXML(File outputFile) {
        boolean success = false;
        boolean canWrite = true;
        if (outputFile.exists()){
            canWrite = MainControl.confirmationDialog("Overwrite " + outputFile.getName() +
                    " in directory " + outputFile.getAbsolutePath() + "?");
        }
        if (canWrite){
            JAXBContext context;
            try {
                context = JAXBContext.newInstance(Collection.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(collection, outputFile);
                success = true;
            } catch (JAXBException e) {
                Gui.feedbackDialog(Alert.AlertType.ERROR, "Error while exporting session data to XML", "");
            }
        }
        return success;
    }

    @XmlRootElement(name = LuaToXML.GMAF_COLLECTION)
    static class Collection {
        private Session session;

        Collection() {
        }

        protected void setSession(Session session) {
            this.session = session;
        }

        @XmlElement(name = LuaToXML.GMAF_DATA)
        Session getSession() {
            return session;
        }

    }
}
