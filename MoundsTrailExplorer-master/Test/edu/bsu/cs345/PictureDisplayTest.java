package edu.bsu.cs345;

import org.junit.Before;
import org.junit.Test;

public class PictureDisplayTest {

    TrailSelectionOption option = new TrailSelectionOption();

    @Before
    public void createPictureDisplay(){
        PictureDisplay display = new PictureDisplay(option.getListOfPicturesGivenTrailNumber(1));
        display.createFirstImage();
    }
}
