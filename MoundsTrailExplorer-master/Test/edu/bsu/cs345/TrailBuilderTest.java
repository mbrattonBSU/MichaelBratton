package edu.bsu.cs345;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TrailBuilderTest {
    TrailBuilder trailBuilder;
    @Before
    public void buildTrailBuilder() {
        trailBuilder = new TrailBuilder();
    }

    @Test
    public void testBuildTrailArray() {
        ArrayList<Trail> trailList = new ArrayList<>();
        for (int i = 1; i < 7; i++){
            Trail trail = new Trail();
            trail.setTrailName("Trail " + i);
            trailList.add(trail);
        }
        trailBuilder.buildTrails();
        assertEquals("Trail 1", trailBuilder.getListOfTrails().get(0).getTrailName());

    }

    @Test
    public void testCorrectIfTrailsIsEmpty(){
        assertEquals(true, trailBuilder.checkIfListOfTrailsIsEmpty());
    }

    @Test
    public void testCorrectIfTrailsIsNotEmpty(){
        trailBuilder.buildTrails();
        assertEquals(false, trailBuilder.checkIfListOfTrailsIsEmpty());
    }

    @Test
    public void testIfTrailBuilderReturnsCorrectValue(){
        assertEquals("Trail 1" ,trailBuilder.getTrailInformation(1));
    }


}
