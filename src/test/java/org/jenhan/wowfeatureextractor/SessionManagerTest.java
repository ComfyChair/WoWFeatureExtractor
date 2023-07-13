package org.jenhan.wowfeatureextractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionManagerTest {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    SessionManager testManager;
    final File shortSessionsFile = new File("src/test/resources/ShortSessionsTest.lua");
    final File mediumLengthFile = new File("src/test/resources/MediumLengthSession.lua");
    final List<Session> expectedList = new ArrayList<>();
    Validator validator;
    XmlErrorHandler xsdErrorHandler;
    private final static File xsdFile = new File("src/test/resources/gmaf-interaction.xsd");

    @BeforeEach
    void setUp() throws SAXException {
        testManager = SessionManager.getInstance();
        validator = initValidator();
        xsdErrorHandler = new XmlErrorHandler();
        validator.setErrorHandler(xsdErrorHandler);
        List<LuaReaderTest.SessionData> sessionData = new ArrayList<>();
        LuaReaderTest.SessionData session_0 = new LuaReaderTest.SessionData("Antigone", "TestServer", new Date(1688840368L *1000));
        LuaReaderTest.SessionData session_1 = new LuaReaderTest.SessionData("Spice", "Sen'jin", new Date(1688891052L * 1000));
        LuaReaderTest.SessionData session_2 = new LuaReaderTest.SessionData("Antigone","Sen'jin", new Date(1688840385L * 1000));
        LuaReaderTest.SessionData session_3 = new LuaReaderTest.SessionData("Antigone", "Sen'jin", new Date(1688840200L * 1000));
        Collections.addAll(sessionData, session_0, session_1, session_2, session_3);
        for (int i = 0; i < 4; i++) {
            Session newSession = Session.create(i, shortSessionsFile.getName());
            expectedList.add(newSession);
            LuaReaderTest.SessionData thisSessionData = sessionData.get(i);
            newSession.setCharName(thisSessionData.charName());
            newSession.setDateTime(thisSessionData.date());
            newSession.setServerName(thisSessionData.serverName());
        }
    }

    @Test
    void getInstance() {
        SessionManager sm1 = SessionManager.getInstance();
        SessionManager sm2 = SessionManager.getInstance();
        assertEquals(testManager, sm1);
        assertEquals(testManager, sm2);
    }

    @Test
    void getSessionList() {
        List<Session> actualList = testManager.getSessionList(shortSessionsFile);
        for (int i = 0; i < 4; i++) {
            Session actual = actualList.get(i);
            Session expected = expectedList.get(i);
            assertEquals(expected.sessionIDProperty().get(), actual.sessionIDProperty().get());
            assertEquals(expected.getFileName(), actual.getFileName());
            assertEquals(expected.charNameProperty().get(), actual.charNameProperty().get());
            assertEquals(expected.serverNameProperty().get(), actual.serverNameProperty().get());
            assertEquals(expected.dateProperty().get().toString(), actual.dateProperty().get().toString());
            assertEquals(expected.startTimeProperty().get().toString(), actual.startTimeProperty().get().toString());
        }
    }

    @Test
    void exportToXML() throws IOException, SAXException {
        String outPath = "src/test/testOutput/test.xml";
        File outFile = new File(outPath);
        testManager.getSessionList(shortSessionsFile);
        // single session export
        List<File> outList = testManager.exportToXML(outFile, List.of(0));
        validate(outList.get(0));
        // multiple session export
        outList = testManager.exportToXML(outFile, Arrays.asList(1, 2, 3));
        for (File file : outList
        ) {
            validate(file);
        }
        assertEquals(0, xsdErrorHandler.getExceptions().size());
    }

    @Test
    void validateMediumLengthExport() throws IOException, SAXException {
        String outPath = "src/test/testOutput/mediumLength.xml";
        File outFile = new File(outPath);
        testManager.getSessionList(mediumLengthFile);
        // single session export
        List<File> outList = testManager.exportToXML(outFile, List.of(0));
        validate(outList.get(0));
        assertEquals(0, xsdErrorHandler.getExceptions().size());
    }

    private void validate(File outFile) throws SAXException, IOException {
        validator.validate(new StreamSource(outFile));
        xsdErrorHandler.getExceptions().forEach(e -> log.info(outFile.getName() + ": " + e.getMessage()));
    }

    /** initializes a xml validator **/
    private Validator initValidator() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(xsdFile);
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
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