package edu.bsu.cs222.FPBreetlison.Model;

import edu.bsu.cs222.FPBreetlison.Controller.BattleController;
import edu.bsu.cs222.FPBreetlison.Controller.OverworldController;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Room;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLogic implements java.io.Serializable {

    private GameData gameData;
    private BattleLogic battleLogic;
    private BattleController battleControl;
    private Stage currentStage;
    private boolean battleUnderway;


    //region Initialization
    public void init(GameData gameData){
        this.gameData = gameData;
        this.gameData.setCurrentRoom(gameData.getAllRooms().get("Colossal Plains"));
        currentStage.setResizable(true);
        setUpOverworld();
    }

    void setUpOverworld() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            Parent root = fxmlLoader.load(getClass().getResource("/edu/bsu/cs222/FPBreetlison/View/OverworldUI.fxml").openStream());
            setOverworldAsStage(root);
            updateStageTitle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OverworldController overworld = fxmlLoader.getController();
        overworld.initialize(this);
    }
    private void setOverworldAsStage(Parent root){
        currentStage.getIcons().add(new Image("/images/battleGraphics/itemGraphics/item_undefined.png"));
        currentStage.setScene(new Scene(root, 900, 600));
        currentStage.show();

    }

    public void updateStageTitle(){
        currentStage.setTitle("Overworld: "+ gameData.getCurrentRoom().getName());
    }
    //endregion

    public void play(){
    }

    public void createBattle(){
        createBattleLogic();
        //battleLogic.start();
        createBattleController();

    }

    private void createBattleLogic(){
        battleLogic = new BattleLogic();
    }

    private void createBattleController(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            Parent root = fxmlLoader.load(getClass().getResource("/edu/bsu/cs222/FPBreetlison/View/BattleUI.fxml").openStream());
            setBattleAsStage(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        battleControl = fxmlLoader.getController();
        battleControl.initialize(this);
    }

    private void setBattleAsStage(Parent root){
        currentStage.setTitle("Battle!");
        battleUnderway = true;
        currentStage.setScene(new Scene(root, 900,600));

    }

    public void travelNorth() {
        if(gameData.getCurrentRoom().getNorth().equals("null")) {
            return;
        }
        gameData.setCurrentRoom(gameData.getAllRooms().get(gameData.getCurrentRoom().getNorth()));

    }

    public void travelSouth() {
        if(gameData.getCurrentRoom().getSouth().equals("null")){
            return;
        }
        gameData.setCurrentRoom(gameData.getAllRooms().get(gameData.getCurrentRoom().getSouth()));
    }

    public void travelEast() {
        if(gameData.getCurrentRoom().getEast().equals("null")) {
            return;
        }
        gameData.setCurrentRoom(gameData.getAllRooms().get(gameData.getCurrentRoom().getEast()));

    }

    public void travelWest() {
        if(gameData.getCurrentRoom().getWest().equals("null")) {
            return;
        }
        gameData.setCurrentRoom(gameData.getAllRooms().get(gameData.getCurrentRoom().getWest()));
    }

    public void saveGame() throws IOException {
        FileOutputStream saveFile = new FileOutputStream("saveFile.sav");
        ObjectOutputStream save = new ObjectOutputStream(saveFile);
        save.writeObject(gameData);
        save.close();
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        try {
            FileInputStream saveFile = new FileInputStream("saveFile.sav");
            ObjectInputStream restore = new ObjectInputStream(saveFile);
            gameData = (GameData)restore.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void setStage(Stage stage){
        currentStage = stage;
    }
    boolean isBattleUnderway(){
        return battleUnderway;
    }
    void setBattleUnderway(){
        battleUnderway = false;
    }
    public GameData getGameData(){
        return gameData;
    }
    BattleController getBattleControl() {
        return battleControl;
    }
    public void setBattleControl(BattleController battleController){
        this.battleControl = battleController;
    }
    public BattleLogic getBattleLogic() {
        return battleLogic;
    }
    public HashMap<Integer, Boolean> checkAvailableDirections() {

        HashMap<Integer, Boolean> availableDirections = new HashMap<>();
        ArrayList<String> allDirections = new ArrayList<>();
        Room currentRoom = gameData.getCurrentRoom();

        allDirections.add(currentRoom.getNorth());
        allDirections.add(currentRoom.getSouth());
        allDirections.add(currentRoom.getEast());
        allDirections.add(currentRoom.getWest());

        for (int i = 0; i<allDirections.size();i++){
            String roomDirection = allDirections.get(i);
            if (roomDirection.equals("null")) {
                availableDirections.put(i,false);
            }
            else{
                availableDirections.put(i,true);
            }
        }

        return  availableDirections;

    }

}
