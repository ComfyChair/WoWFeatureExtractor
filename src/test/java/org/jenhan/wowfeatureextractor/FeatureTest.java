package org.jenhan.wowfeatureextractor;

import org.jenhan.wowfeatureextractor.Util.TimeFormatted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FeatureTest {
    Feature testFeature;
    Feature emptyFeature;
    String TEST_TYPE = "test type";
    String TEST_DESCRIPTION = "test description";
    String TEST_TERM = "test term";
    String ANOTHER_TERM = "another term";
    Date randomDate;

    @BeforeEach
    void setUp() {
        emptyFeature = new Feature();
        testFeature = new Feature();
        testFeature.setType(TEST_TYPE);
        testFeature.setDescription(TEST_DESCRIPTION);
        Random rnd = new Random();
        randomDate = new Date(rnd.nextLong());
        testFeature.setBeginTime(randomDate);
    }

    @Test
    void getDescription() {
        assertEquals(TEST_DESCRIPTION, testFeature.getDescription());
        assertEquals(Feature.DEFAULT_DESCR, emptyFeature.getDescription());
    }

    @Test
    void setDescription() {
        String newDescription = "new description";
        testFeature.setDescription(newDescription);
        assertEquals(newDescription, testFeature.getDescription());
    }

    @Test
    void getBeginTime() {
        TimeFormatted beginTime = new TimeFormatted(randomDate);
        assertEquals(beginTime.toString(), testFeature.getBeginTime());
    }

    @Test
    void setBeginTime() {
        Random rnd = new Random();
        Date newDate = new Date(rnd.nextLong());
        testFeature.setBeginTime(newDate);
        assertEquals((new TimeFormatted(newDate)).toString(), testFeature.getBeginTime());
    }

    @Test
    void getType() {
        assertEquals(TEST_TYPE, testFeature.getType());
        assertEquals(Feature.DEFAULT_TYPE, emptyFeature.getType());
    }

    @Test
    void setType() {
        for (FeatureType featureType: FeatureType.values()) {
            testFeature.setType(featureType.toString());
            assertEquals(featureType.toString(), testFeature.getType());
        }
    }

    @Test
    void getObjectList() {
        assertEquals(0, testFeature.getObjectList().size());
    }

    @Test
    void addObject() {
        Feature.FeatureObject featureObject = new Feature.FeatureObject(0, TEST_TERM);
        testFeature.addObject(featureObject);
        assertEquals(1, testFeature.getObjectList().size());
        assertEquals(featureObject, testFeature.getObjectList().get(0));
        Feature.FeatureObject anotherObject = new Feature.FeatureObject(1, ANOTHER_TERM);
        testFeature.addObject(anotherObject);
        assertEquals(2, testFeature.getObjectList().size());
        assertEquals(anotherObject, testFeature.getObjectList().get(1));
    }

    /** tests for static inner class FeatureObject **/
    @Test
    void testFeatureObjStdConstructor(){
        Feature.FeatureObject featureObject = new Feature.FeatureObject();
        assertEquals(featureObject.getId(), 0);
        assertEquals(featureObject.getProbability(), 0d);
        assertNull(featureObject.getTerm());
    }

    @Test
    void testFeatureObjectEquals(){
        Feature.FeatureObject featureObject = new Feature.FeatureObject(0, TEST_TERM);
        Feature.FeatureObject sameObject = new Feature.FeatureObject(0, TEST_TERM);
        Feature.FeatureObject anotherObject = new Feature.FeatureObject(1, ANOTHER_TERM);
        assertEquals(featureObject, sameObject);
        assertNotEquals(featureObject, anotherObject);
    }

    @Test
    void testFeatureObjectHash(){
        Feature.FeatureObject featureObject = new Feature.FeatureObject(0, TEST_TERM);
        Feature.FeatureObject sameObject = new Feature.FeatureObject(0, TEST_TERM);
        Feature.FeatureObject anotherObject = new Feature.FeatureObject(1, ANOTHER_TERM);
        assertEquals(featureObject.hashCode(), sameObject.hashCode());
        assertNotEquals(featureObject.hashCode(), anotherObject.hashCode());
    }

    /** enum data structure for allowed types with standard description **/
    enum FeatureType {
        MOV_1, MOV_2, ZONE_1, ZONE_2, ZONE_3, OBJ_1, OBJ_2,
        NPC_1, NPC_2, COMM_1, COMM_2, COMM_3, COMM_4,
        GRP_1, SPELL_1,
    }

}