package org.jenhan;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;

import static org.junit.jupiter.api.Assertions.*;

class LuaToXMLTest {
    String testFilePath = "/home/jenny/IdeaProjects/WoWFeatureExtractionTool/testRessources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);
    LineNumberReader reader;

    @BeforeEach
    void setUp() {
        reader = LuaToXML.getNumberReader(testFile);
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
        assertEquals("serverName", LuaToXML.getLuaFieldKey(lineFive));
        String lineSix = reader.readLine();
        assertEquals("featureTable", LuaToXML.getLuaFieldKey(lineSix));
    }

    @Test
    void getLuaFieldValue() throws IOException {
        // discard three lines at the beginning
        for (int i = 0; i < 3; i++) {
            reader.readLine();
        }
        String lineFour = reader.readLine();
        assertEquals("Nepi", LuaToXML.getLuaFieldValue(lineFour));
        String lineFive = reader.readLine();
        assertEquals("Sen'jin", LuaToXML.getLuaFieldValue(lineFive));
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
    void exportToXML() {
    }

}