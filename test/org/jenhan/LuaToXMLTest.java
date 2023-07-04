package org.jenhan;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;

import static org.junit.jupiter.api.Assertions.*;

class LuaToXMLTest {
    String testFilePath = "/home/jenny/IdeaProjects/WoWFeatureExtractionTool/testRessources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);

    @Test
    void exportToXML() {
    }

    @Test
    void getNumberReader() {
        LineNumberReader reader = LuaToXML.getNumberReader(testFile);
        assertInstanceOf(LineNumberReader.class, reader);
    }

    @Test
    void readLines() throws IOException {
        LineNumberReader reader = LuaToXML.getNumberReader(testFile);
        // first line should be empty
        String firstLine = reader.readLine();
        assertEquals("", firstLine);
        // second line starts the Lua table
        String secondLine = reader.readLine();
        assertEquals("FRT_FeatureRecordings = {", secondLine);
        // third line starts a session
        String thirdLine = reader.readLine();
        thirdLine = thirdLine.trim();
        assertEquals("[\"session_", thirdLine.substring(0,10));
    }
}