package edu.bsu.cs345;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.Image;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class TrailSelectionTest{

    TrailSelectionOption option = new TrailSelectionOption();

    @Before
    public void createPanel(){
        JFXPanel jfxPanel = new JFXPanel();
    }

    @Test
    public void testGetsCorrectNumberOfTrailImages(){
        ArrayList<Image> answer = option.getListOfPicturesGivenTrailNumber(1);
        assertEquals(3, answer.size());
    }

}
