package org.jenhan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;

import static org.junit.jupiter.api.Assertions.*;

class LuaToXMLTest {
    String testFilePath = "/home/jenny/IdeaProjects/WoWFeatureExtractionTool/testRessources/TestInput1.lua";
    File testFile = new File(testFilePath);
    LineNumberReader reader;

    @BeforeEach
    void setUp() {
        reader = LuaToXML.getReader(testFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        reader.close();
    }

    @Test
    void getNumberReader() {
        assertInstanceOf(LineNumberReader.class, reader);
    }

    @Test
    void isAssignment() {
        assertTrue(LuaToXML.isAssignment("Egon = doof"));
        assertTrue(LuaToXML.isAssignment("[\"startTimeStamp\"] = 1688389927,"));
        assertTrue(LuaToXML.isAssignment(				"[\"description\"] = \"entered new subarea\","));
        assertTrue(LuaToXML.isAssignment("\t\t[\"characterName\"] = \"Arvensis\","));
    }



    @Test
    void getLuaFieldKey() throws IOException {
        // discard two lines at the beginning
        reader.readLine();
        reader.readLine();
        String lineThree = reader.readLine();
        assertEquals("session_2", LuaToXML.getLuaFieldKey(lineThree));
        String lineFour = reader.readLine();
        assertEquals("characterName", LuaToXML.getLuaFieldKey(lineFour));
        String lineFive = reader.readLine();
        assertEquals("dateTime", LuaToXML.getLuaFieldKey(lineFive));
        String lineSix = reader.readLine();
        assertEquals("serverName", LuaToXML.getLuaFieldKey(lineSix));
        String lineSeven = reader.readLine();
        assertEquals("startTimeStamp", LuaToXML.getLuaFieldKey(lineSeven));
        String lineEight = reader.readLine();
        assertEquals("featureTable", LuaToXML.getLuaFieldKey(lineEight));
    }

    @Test
    void getLuaFieldValue() throws IOException {
        // discard three lines at the beginning
        for (int i = 0; i < 3; i++) {
            reader.readLine();
        }
        String lineFour = reader.readLine();
        assertEquals("Arvensis", LuaToXML.getLuaFieldValue(lineFour));
        String lineFive = reader.readLine();
        // don't test the date, might get edited in the future
        String lineSix = reader.readLine();
        assertEquals("Sen'jin", LuaToXML.getLuaFieldValue(lineSix));
    }

    @Test
    void readLines() throws IOException {
        // check line number
        int lineNumber = reader.getLineNumber();
        assertEquals(0, lineNumber);
        // first line should be empty
        String firstLine = reader.readLine();
        assertEquals("", firstLine);
        // check line number
        lineNumber = reader.getLineNumber();
        assertEquals(1, lineNumber);
        // second line starts the Lua table
        String secondLine = reader.readLine();
        assertEquals("FRT_FeatureRecordings = {", secondLine);
        // check line number
        lineNumber = reader.getLineNumber();
        assertEquals(2, lineNumber);
        // third line starts a session
        String thirdLine = reader.readLine();
        thirdLine = thirdLine.trim();
        assertEquals("[\"session_", thirdLine.substring(0,10));
        // check line number
        lineNumber = reader.getLineNumber();
        assertEquals(3, lineNumber);
    }

    @Test
    void exportToXML_checkWellformed() throws IOException, SAXException, ParserConfigurationException {
        File testOutput_1 = new File("testOutput/exportTest1.xml");
        String testFilePath_1 = "testRessources/TestInput1.lua";
        File testFile_1 = new File(testFilePath_1);
        Session session_1 = new Session(Session.readSessionInfo(testFile_1).get(0));
        session_1.exportToXML(testFile_1, testOutput_1);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        SAXParser parser = factory.newSAXParser();

        XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(new SimpleErrorHandler());
        reader.parse(testOutput_1.getAbsolutePath());
    }

    public class SimpleErrorHandler implements ErrorHandler {
        public void warning(SAXParseException e) {
            System.out.println(e.getMessage());
        }

        public void error(SAXParseException e) {
            System.out.println(e.getMessage());
        }

        public void fatalError(SAXParseException e) {
            System.out.println(e.getMessage());
        }
    }

}