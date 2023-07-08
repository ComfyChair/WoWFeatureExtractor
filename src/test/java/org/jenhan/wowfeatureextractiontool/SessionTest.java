package org.jenhan.wowfeatureextractiontool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {
    String testFilePath = "src/test/resources/FeatureRecordingTool.lua";
    File testFile = new File(testFilePath);
    List<SessionInfo> sessionInfoList;
    List<SessionInfo> expectedInfoList;

    @BeforeEach
    void setUp() {
        sessionInfoList = Session.readSessionInfo(testFile);
        expectedInfoList = new ArrayList<>();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688494496000L));
        SessionInfo expectedSession1 = new SessionInfo(0, 3);
        expectedSession1.setContentProperties("Arvensis", "Sen'jin", startTime, 8);
        expectedInfoList.add(expectedSession1);
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688544677000L));
        SessionInfo expectedSession2 = new SessionInfo(1, 389);
        expectedSession2.setContentProperties("Sugi", "Sen'jin", startTime, 393);
        expectedInfoList.add(expectedSession2);
        startTime = Calendar.getInstance();
        startTime.setTime(new Date(1688494046000L));
        SessionInfo expectedSession3 = new SessionInfo(2, 571);
        expectedSession3.setContentProperties("Arvensis", "Sen'jin", startTime, 576);
        expectedInfoList.add(expectedSession3);
    }

    @Test
    void getSessionInfo() {
        Session newSession = new Session(sessionInfoList.get(0));
        assertEquals(expectedInfoList.get(0), newSession.getSessionInfo());
    }

    @Test
    void readSessionInfo1() {
        assertEquals(3, sessionInfoList.size());
        // first session
        assertEquals(expectedInfoList.get(0), sessionInfoList.get(0));
        // second session
        assertEquals(expectedInfoList.get(1), sessionInfoList.get(1));
        // third session
        assertEquals(expectedInfoList.get(2), sessionInfoList.get(2));
    }

    @Test
    void exportToXML_TestStartOfDocument() throws IOException {
        File testOutput = new File("src/test/testOutput/exportTest2.xml");
        Session session = new Session(sessionInfoList.get(1));
        System.out.println("Testing start of document: " + testOutput.getPath());
        System.out.println("Session Info: " + sessionInfoList.get(1));
        session.exportToXML(testFile, testOutput);
        File outputPretty = prettyPrintXML(new File("src/test/testOutput/exportTest2.xml"));
        // check header
        BufferedReader reader = new BufferedReader(new FileReader(outputPretty));
        String headerLine = reader.readLine().trim();
        String[] split = headerLine.split("><");
        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?", split[0].toLowerCase());
        assertEquals("gmaf-collection>", split[1]);
        String dataLine = reader.readLine().trim();
        assertEquals("<gmaf-data>", dataLine);
        String fileLine = reader.readLine().trim();
        assertEquals("<file>" + testOutput.getName() + "</file>", fileLine);
        String dateLine = reader.readLine().trim();
        assertTrue(isSimpleTag(dateLine, "date"));
    }

    private boolean isSimpleTag(String line, String tagString) {
        return line.startsWith("<" + tagString + ">") && line.endsWith("</" + tagString + ">");
    }

    private boolean isOpenTag(String line, String tagString) {
        return line.startsWith("<" + tagString) && line.endsWith(">");
    }

    @Test
    void timeConversionTest(){
        Calendar testTime = Calendar.getInstance();
        long unixTime = 1688372354L * 1000;
        testTime.setTime(new Date(unixTime));
        int date = testTime.get(Calendar.DATE);
        assertEquals(3, date);
        int month = testTime.get(Calendar.MONTH);
        assertEquals(6, month); // JAN = 0
    }

    public static File prettyPrintXML(File xmlFile) {
        try {
            File outFile = new File(xmlFile.getPath().replace(".xml", "_pretty.xml"));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            FileInputStream inputStream = new FileInputStream(xmlFile);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamSource src = new StreamSource(inputStream);
            StreamResult result = new StreamResult(new BufferedOutputStream(new FileOutputStream(outFile)));
            transformer.transform(src, result);
            return outFile;
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlFile, e);
        }
    }
}