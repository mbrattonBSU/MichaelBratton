package edu.bsu.cs345;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;


public class Controller {

    public ImageView pictureDisplayBox;
    public Label trailTitleLabel;

    private TrailSelectionOption trailSelectionOption = new TrailSelectionOption();
    private ArrayList<Image> currentSelection;
    private PictureDisplay currentDisplay;
    private String currentTrailTitle;

    @FXML
    public void initialize() {
        pictureDisplayBox.setImage(new Image("StartingImage/MoundsTrailExplorerBenchPicture.jpg"));
    }

    public void trailButtonSelected(ActionEvent actionEvent) {
        Button selectedTrailButton = (Button) actionEvent.getSource();
        int trailNumber = Integer.parseInt(selectedTrailButton.getId());
        if (currentTrailTitle != trailSelectionOption.getTrailInformationGivenTrailNumber(trailNumber)) {
            currentSelection = trailSelectionOption.getListOfPicturesGivenTrailNumber(trailNumber);
            currentTrailTitle = trailSelectionOption.getTrailInformationGivenTrailNumber(trailNumber);
            currentDisplay = new PictureDisplay(currentSelection);
            pictureDisplayBox.setImage(currentDisplay.createFirstImage());
            trailTitleLabel.setText(currentTrailTitle);
        }

    }

    public void previousButtonSelected(ActionEvent actionEvent) {
        if (currentDisplay != null) {
            pictureDisplayBox.setImage(currentDisplay.getPreviousImage());
        }
    }

    public void nextButtonSelected(ActionEvent actionEvent) {
        if (currentDisplay != null) {
            pictureDisplayBox.setImage(currentDisplay.getNextImage());
        }
    }
}
