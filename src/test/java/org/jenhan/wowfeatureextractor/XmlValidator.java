package org.jenhan.wowfeatureextractor;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class XmlValidator {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    Validator validator;
    XmlErrorHandler errorHandler;

    XmlValidator(File xsdFile)throws SAXException {
        /** initializes a xml validator **/
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(xsdFile);
        Schema schema = factory.newSchema(schemaFile);
        validator = schema.newValidator();
        errorHandler = new XmlValidator.XmlErrorHandler();
        validator.setErrorHandler(errorHandler);
    }

    int validate(File outFile) throws SAXException, IOException {
        validator.validate(new StreamSource(outFile));
        errorHandler.getExceptions().forEach(e -> log.info(outFile.getName() + ": " + e.getMessage()));
        return errorHandler.getExceptions().size();
    }

    static class XmlErrorHandler implements ErrorHandler {

        private final List<SAXParseException> exceptions;

        public XmlErrorHandler() {
            this.exceptions = new ArrayList<>();
        }

        public List<SAXParseException> getExceptions() {
            return exceptions;
        }

        @Override
        public void warning(SAXParseException exception) {
            exceptions.add(exception);
        }

        @Override
        public void error(SAXParseException exception) {
            exceptions.add(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) {
            exceptions.add(exception);
        }
    }
}
