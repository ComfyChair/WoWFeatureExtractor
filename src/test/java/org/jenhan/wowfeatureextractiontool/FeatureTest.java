package org.jenhan.wowfeatureextractiontool;

import org.jenhan.wowfeatureextractiontool.Util.TimeFormatted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureTest {
    Feature testFeature;
    String TEST_TYPE = "test type";
    String TEST_DESCRIPTION = "test description";
    String TEST_TERM = "test term";
    String ANOTHER_TERM = "another term";
    Date randomDate;

    @BeforeEach
    void setUp() {
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
        assertEquals(Feature.FeatureType.UNKNOWN, testFeature.getType());
    }

    @Test
    void setType() {
        for (Feature.FeatureType featureType: Feature.FeatureType.values()
             ) {
            testFeature.setType(featureType.toString());
            assertEquals(featureType, testFeature.getType());
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

}