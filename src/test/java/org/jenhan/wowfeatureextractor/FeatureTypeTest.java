package org.jenhan.wowfeatureextractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** test validation of xml output from input files with specific features **/
public class FeatureTypeTest {
    private SessionManager testManager;
    private final List<File> inputFileList = new ArrayList<>();
    private final List<File> outputFileList = new ArrayList<>();
    /** test resource constants **/
    private final static File emoteTest = new File("src/test/resources/EmoteTest.lua");
    private final static File flightMasterTest = new File("src/test/resources/FlightmasterTest.lua");
    private final static File groupingTest = new File("src/test/resources/GroupingTest.lua");
    private final static File lootTest = new File("src/test/resources/LootTest.lua");
    private final static File mailboxTest = new File("src/test/resources/MailboxTest.lua");
    private final static File moveTest = new File("src/test/resources/MoveTest.lua");
    private final static File partyChatTest = new File("src/test/resources/PartyChatTest.lua");
    private final static File questTest = new File("src/test/resources/QuestTest.lua");
    private final static File spellcastTest = new File("src/test/resources/SpellcastTest.lua");
    private final static File whisperTest = new File("src/test/resources/WhisperTest.lua");
    private final static File zoningTest = new File("src/test/resources/Zone1_Zone3_test.lua");
    private final static File zoningTest2 = new File("src/test/resources/ZoneSubzoneTest.lua");
    private XmlValidator validator;
    private final static File xsdFile = new File("src/test/resources/gmaf-interaction.xsd");

    @BeforeEach
    void setUp() throws SAXException {
        testManager = SessionManager.getInstance();
        // set up validator
        validator = new XmlValidator(xsdFile);
        // setup input
        inputFileList.addAll(List.of(emoteTest, flightMasterTest, groupingTest, lootTest, mailboxTest, moveTest,
                partyChatTest, questTest, spellcastTest, whisperTest, zoningTest, zoningTest2));
    }

    @Test
    void featureTypeValidation() throws IOException, SAXException {
        for (File luaFile: inputFileList
             ) {
            testManager.getSessionList(luaFile);
            String outPath = ("src/test/testOutput/" + luaFile.getName()).replace(".lua", "") + ".xml";
            File outFile = new File(outPath);
            // single session export
            List<File> outList = testManager.exportToXML(outFile, List.of(0));
            int exceptions = validator.validate(outList.get(0));
            outputFileList.addAll(outList);
            assertEquals(0, exceptions);
        }
        assertEquals(inputFileList.size(), outputFileList.size());
    }

}
