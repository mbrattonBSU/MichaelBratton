package edu.bsu.cs345;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TrailTest {
    private Trail trail = new Trail();
    @Test
    public void checkTrailSetterAndGetter(){
        trail.setTrailName("Trail 1");
        assertEquals("Trail 1", trail.getTrailName());
    }





}
