package edu.bsu.cs345;

import javafx.scene.image.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Objects;

class TrailSelectionOption {

    private TrailBuilder builder = new TrailBuilder();

    String getTrailInformationGivenTrailNumber(int trailNumber) {
            return builder.getTrailInformation(trailNumber);
        }

    //Some of the picture files uploaded by phones are stored a little differently.
    // They don't cause any issues with viewing so we have surpressed these warnings.
    ArrayList<Image> getListOfPicturesGivenTrailNumber(int trailNumber) {
            File directory = new File("./resources/Trail " + trailNumber);
            String[] fileExtension = new String[]{"jpg","JPG", "png"};
            FilenameFilter fileFilter = createFileFilter(fileExtension);
            return createImageList(directory, fileFilter);
        }

        private ArrayList<Image> createImageList(File directory, FilenameFilter fileFilter){
            ArrayList<Image> listOfAllPicturesInOneTrail = new ArrayList<>();
            if (directory.isDirectory()){
                for (File f: Objects.requireNonNull(directory.listFiles(fileFilter))){
                    Image image = new Image(f.toURI().toString());
                    listOfAllPicturesInOneTrail.add(image);
                }
            }
            return listOfAllPicturesInOneTrail;
        }

        private FilenameFilter createFileFilter(String[] fileExtension){
            return (dir, name) -> {
                for (String aFileExtension : fileExtension)
                    if (name.endsWith("." + aFileExtension)) {
                        return true;
                    }
                return false;
            };
        }
    }
