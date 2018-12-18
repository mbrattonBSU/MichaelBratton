package edu.bsu.cs345;
import javafx.scene.image.Image;
import java.util.ArrayList;

class PictureDisplay {

        private ArrayList<Image> trailPicturesArray = new ArrayList<>();
        private int imagesIndex = 0;

        PictureDisplay(ArrayList<Image> trailImages){
            for (Image trailImage : trailImages) {
                swapToImageView(trailImage);
            }
        }

        private void swapToImageView(Image trailImage) {
            trailPicturesArray.add(trailImage);
        }

    Image createFirstImage() {
            return trailPicturesArray.get(imagesIndex);
        }

    Image getNextImage() {
            if (imagesIndex == trailPicturesArray.size() - 1){
                imagesIndex = 0;
            }
            else{
                imagesIndex++;
            }
            return trailPicturesArray.get(imagesIndex);
        }

    Image getPreviousImage() {
            if (imagesIndex == 0){
                imagesIndex = trailPicturesArray.size()-1;
            }
            else{
                imagesIndex--;
            }
            return trailPicturesArray.get(imagesIndex);
        }

    }

