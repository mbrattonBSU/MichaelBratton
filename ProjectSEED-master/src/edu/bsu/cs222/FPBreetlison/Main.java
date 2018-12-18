package edu.bsu.cs222.FPBreetlison;

import edu.bsu.cs222.FPBreetlison.Model.GameLogic;
import edu.bsu.cs222.FPBreetlison.Model.GameData;
import javafx.application.Application;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main extends Application {

private GameData gameData = new GameData();
private GameLogic game = new GameLogic();

    public Main() throws ParserConfigurationException, SAXException, IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        game.setStage(primaryStage);
        game.init(gameData);
        game.play();
    }


    public static void main(String[] args) {
        launch(args);

    }
}
