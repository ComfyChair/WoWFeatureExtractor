package org.jenhan.wowfeatureextractor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** test validation of xml output from input files with specific features **/
public class FeatureTypeTest {
    private SessionManager testManager;
    private final Map<File, String> inputFileMap = new HashMap<>();
    private final Map<File, String> outputFileMap = new HashMap<>();
    /** test resource constants **/
    // movement
    private final static File startMovingTest = new File("src/test/resources/ZoneIndoorTest.lua");
    private final static File stopMovingTest = new File("src/test/resources/ZoneIndoorTest.lua");
    private final static File zoneAreaTest = new File("src/test/resources/ZoneTest.lua");
    private final static File zoneSubareaTest = new File("src/test/resources/ZoneTest.lua");
    private final static File zoneIndoorsTest = new File("src/test/resources/ZoneIndoorTest.lua");
    //objects
    private final static File lootTest = new File("src/test/resources/LootTest.lua");
    private final static File mailboxTest = new File("src/test/resources/MailboxTest.lua");
    private final static File questTest = new File("src/test/resources/QuestTest.lua");
    private final static File flightMasterTest = new File("src/test/resources/FlightmasterTest.lua");
    // player character interactions
    private final static File emoteTest = new File("src/test/resources/EmoteTest.lua");
    private final static File whisperSendTest = new File("src/test/resources/WhisperTest.lua");
    private final static File whisperReceivedTest = new File("src/test/resources/WhisperTest.lua");
    private final static File partyChatTest = new File("src/test/resources/PartyChatTest.lua");
    private final static File partyLeadTest = new File("src/test/resources/PartyLeadTest.lua");
    private final static File groupingTest = new File("src/test/resources/GroupingTest.lua");
    //spellcast
    private final static File spellcastTest = new File("src/test/resources/SpellcastTest.lua");


    private XmlValidator validator;
    private final static File xsdFile = new File("src/test/resources/gmaf-interaction.xsd");

    @BeforeEach
    void setUp() throws SAXException {
        testManager = SessionManager.getInstance();
        // set up validator
        validator = new XmlValidator(xsdFile);
        // setup input
        inputFileMap.put(startMovingTest, "MOV_1");
        inputFileMap.put(stopMovingTest, "MOV_2");
        inputFileMap.put(zoneAreaTest, "ZONE_1");
        inputFileMap.put(zoneSubareaTest, "ZONE_2");
        inputFileMap.put(zoneIndoorsTest, "ZONE_3");
        inputFileMap.put(lootTest, "OBJ_1");
        inputFileMap.put(mailboxTest, "OBJ_2");
        inputFileMap.put(questTest, "NPC_1");
        inputFileMap.put(flightMasterTest, "NPC_2");
        inputFileMap.put(emoteTest, "COMM_1");
        inputFileMap.put(whisperSendTest, "COMM_2");
        inputFileMap.put(whisperReceivedTest, "COMM_3");
        inputFileMap.put(partyChatTest, "COMM_4");
        inputFileMap.put(partyLeadTest, "COMM_4");
        inputFileMap.put(groupingTest, "GRP_1");
        inputFileMap.put(spellcastTest, "SPELL_1");
    }

    /** validate test file conversion for each feature type **/
    @Test
    void featureTypeValidation() throws IOException, SAXException, JAXBException {
        for (Map.Entry<File, String> entry: inputFileMap.entrySet()) {
            testManager.getSessionList(entry.getKey());
            String outPath = ("src/test/testOutput/" +
                    entry.getKey().getName()).replace(".lua", ".xml");
            File outFile = new File(outPath);
            // single session export
            List<File> outList = testManager.exportToXML(outFile, List.of(0));
            int exceptions = validator.validate(outList.get(0));
            outputFileMap.put(outList.get(0), entry.getValue());
            assertEquals(0, exceptions);
        }
        assertEquals(inputFileMap.size(), outputFileMap.size());
        // test if interactions in xml contained an interaction entry of the specified type through unmarshalling
        JAXBContext context = JAXBContext.newInstance(LuaToXML.Collection.class);
        for (Map.Entry<File, String> entry: outputFileMap.entrySet()) {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object collectionObject = unmarshaller.unmarshal(entry.getKey());
            assertEquals(LuaToXML.Collection.class, collectionObject.getClass());
            List<Feature> featureList = ((LuaToXML.Collection) collectionObject).getSession().getFeatureList();
            boolean containsType = false;
            for (Feature feature: featureList
                 ) {
                if (feature.getType().equals(entry.getValue())){
                    containsType = true;
                }
            }
            assertTrue(containsType);
        }
    }

}
