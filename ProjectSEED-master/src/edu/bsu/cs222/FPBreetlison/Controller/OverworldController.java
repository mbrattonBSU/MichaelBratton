package edu.bsu.cs222.FPBreetlison.Controller;

import edu.bsu.cs222.FPBreetlison.Model.Animator;
import edu.bsu.cs222.FPBreetlison.Model.GameData;
import edu.bsu.cs222.FPBreetlison.Model.GameLogic;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Event;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Fighter;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class OverworldController implements java.io.Serializable {


    public VBox sideBar;
    public ImageView partyButton;
    public ImageView sideBarGraphic;
    public ImageView travelButton;
    public ImageView saveLoadButton;
    public ImageView inspectButton;
    public ImageView inventoryItemDisplay;

    public StackPane shopMenu;
    public StackPane travelPane;
    public VBox shopItemsArea;

    public Label roomDescription;
    public Label inventoryNameDisplay;
    public Label inventoryQuickSummaryDisplay;

    public Button north;
    public Button south;
    public Button east;
    public Button west;
    public Button closeItemsButton;
    public Button openItemsButton;
    public HBox backgroundImage;
    public VBox inventoryItemsArea;
    public VBox events;
    public VBox teamArea;
    public VBox standbyArea;
    public StackPane loadSaveMenu;
    public StackPane inspectMenu;
    public StackPane navBanner;
    public StackPane teamMenu;
    public StackPane itemsMenu;
    public ScrollPane inventoryAreaScroller;
    public Label walletDisplay;
    public Label transferButton;

    private boolean movingFromStandby;
    private boolean movingFromTeam;
    private int indexToTransfer;

    private GameLogic game;
    private GameData gameData;
    private Animator animator;
    private Event loadedEvent;

    public void initialize(GameLogic game){
        indexToTransfer = 0;
        this.game = game;
        this.gameData = game.getGameData();
        this.animator = new Animator(game);
        setUpBarGraphic();
        updateRoom();
        initializePartyButton();
        initializeTravelButton();
        initializeSaveLoadButton();
        initializeInspectButton();
        initNavBanner();
    }



    private void initializePartyButton() {
        partyButton.setOnMouseEntered(e->optionHover(partyButton));
        partyButton.setOnMouseExited(e->optionUnhover(partyButton));
        partyButton.setOnMouseClicked(e->openTeamMenu());
        partyButton.setImage(new Image("/images/system/system_sidebar_partymenu.png"));
    }

    private void openTeamMenu() {
        teamMenu.setVisible(true);
        populateTeamArea();
        populateStandbyArea();
    }

    private void populateStandbyArea() {
        standbyArea.getChildren().clear();
        for(int i = 0; i<gameData.getStandby().size();i++){
            int index = i;
            HBox standbyCharacterSelector = new HBox();
            ImageView image = new ImageView(new Image(gameData.getStandby().get(i).getMiniGraphicPath()));
            image.setFitWidth(60);
            image.setFitHeight(60);
            Label name = new Label(gameData.getStandby().get(i).getName());
            standbyCharacterSelector.setOnMousePressed(e->selectStandbyToTransfer(index));
            standbyCharacterSelector.getChildren().addAll(image,name);
            standbyArea.getChildren().add(standbyCharacterSelector);
        }
    }

    private void selectStandbyToTransfer(int index) {
        indexToTransfer = index;
        movingFromStandby = true;
        movingFromTeam = false;
        populateCharacterStats();
    }

    private void populateCharacterStats(){
        Fighter selectedFighter = getFromProperList();
        Label nameLabel = (Label)teamMenu.getChildren().get(2);
        Label levelLabel = (Label)teamMenu.getChildren().get(3);
        Label hpLabel = (Label)teamMenu.getChildren().get(4);
        Label atkLabel = (Label)teamMenu.getChildren().get(5);
        Label defLabel = (Label)teamMenu.getChildren().get(6);
        Label agilityLabel = (Label)teamMenu.getChildren().get(7);
        nameLabel.setText(selectedFighter.getName());
        levelLabel.setText("Lvl: " + selectedFighter.getLvl() );
        hpLabel.setText("HP: "+ selectedFighter.getCurrStats().get("hp")+"/"+selectedFighter.getMaxHP());
        atkLabel.setText("Attack: " + selectedFighter.getAttack());
        defLabel.setText("Def: " + selectedFighter.getDefense());
        agilityLabel.setText("Agility: " + selectedFighter.getAgility());

    }

    private Fighter getFromProperList() {
        Fighter fighter;
        if(movingFromTeam){
            fighter = gameData.getTeam().get(indexToTransfer);
            return fighter;
        }
        else{
            fighter = gameData.getStandby().get(indexToTransfer);
            return fighter;
        }

    }

    private void populateTeamArea() {
        teamArea.getChildren().clear();
        for(int i = 0; i<gameData.getTeam().size();i++){
            int index = i;
            HBox teamCharacterSelector = new HBox();
            ImageView image = new ImageView(new Image(gameData.getTeam().get(i).getMiniGraphicPath()));
            image.setFitWidth(60);
            image.setFitHeight(60);
            Label name = new Label(gameData.getTeam().get(i).getName());
            teamCharacterSelector.setOnMousePressed(e-> selectTeamMember(index));
            teamCharacterSelector.getChildren().addAll(image,name);
            teamArea.getChildren().add(teamCharacterSelector);
        }
    }

    private void selectTeamMember(int index) {
        indexToTransfer = index;
        movingFromTeam = true;
        movingFromStandby = false;
        populateCharacterStats();

    }

    public void handleTransfer() {
        if(movingFromStandby && gameData.getTeam().size()<4){
            gameData.getTeam().add(gameData.getStandby().get(indexToTransfer));
            gameData.getStandby().remove(indexToTransfer);
            movingFromStandby = false;
        }
        if(movingFromTeam && gameData.getTeam().size() > 1){
            gameData.getStandby().add(gameData.getTeam().get(indexToTransfer));
            gameData.getTeam().remove(indexToTransfer);
            movingFromTeam = false;
        }
        populateTeamArea();
        populateStandbyArea();
        gameData.calcTP();
    }

    private void initNavBanner() {
        ImageView bannerImage = (ImageView)navBanner.getChildren().get(0);
        Label bannerDisplay = (Label)navBanner.getChildren().get(1);
        bannerImage.setImage(new Image("/images/system/system_banner.png"));
        bannerDisplay.setText(gameData.getCurrentRoom().getName());
        animator.showBanner(navBanner);

    }

    private void updateBanner(){
        Label bannerDisplay = (Label)navBanner.getChildren().get(1);
        bannerDisplay.setText(gameData.getCurrentRoom().getName());
        animator.showBanner(navBanner);
    }


    private void initializeTravelButton(){
        travelButton.setOnMousePressed(e->openTravel());
        travelButton.setOnMouseEntered(e->optionHover(travelButton));
        travelButton.setOnMouseExited(e->optionUnhover(travelButton));
        travelButton.setImage(new Image("/images/system/system_sidebar_map.png"));
    }



    private void initializeSaveLoadButton(){
        saveLoadButton.setOnMousePressed(e->showSaveMenu());
        saveLoadButton.setOnMouseEntered(e->optionHover(saveLoadButton));
        saveLoadButton.setOnMouseExited(e->optionUnhover(saveLoadButton));
        saveLoadButton.setImage(new Image("/images/system/system_sidebar_save.png"));
    }

    private void initializeInspectButton() {
        inspectButton.setOnMousePressed(e->showInspectMenu());
        inspectButton.setOnMouseEntered(e->optionHover(inspectButton));
        inspectButton.setOnMouseExited(e->optionUnhover(inspectButton));
        inspectButton.setImage(new Image("/images/system/system_sidebar_inspect.png"));
    }

    private void showSaveMenu() {
        loadSaveMenu.setVisible(true);
    }

    private void showInspectMenu() {
        inspectMenu.setVisible(true);
    }

    private void optionHover(ImageView button) {

        button.setScaleX(1.5);
        button.setScaleY(1.5);
    }
    private void optionUnhover(ImageView button){
        button.setScaleX(1);
        button.setScaleY(1);
    }

    private void setUpBarGraphic() {
        sideBarGraphic.setImage(new Image("/images/system/system_sidebar.png"));
    }

    private void updateRoom() {
        roomDescription.setText(gameData.getCurrentRoom().getDescription());
        setDirectionButtonsVisible();
        updateEvents();
        setBackground();
        updateBanner();
        updateWallet();
        game.updateStageTitle();
    }

    private void updateEvents() {
        events.getChildren().clear();
        for(int i = 0; i<gameData.getCurrentRoom().getEvents().size();i++){
            int index = i;
            ImageView event = new ImageView();
            formatEvent(event,index);
            event.setOnMousePressed(e->startEvent(index));
            events.getChildren().add(event);
        }
    }

    private void formatEvent(ImageView event,int index) {
        event.setImage(new Image(gameData.getCurrentRoom().getEvents().get(index).getImagePath()));
        event.setFitWidth(100);
        event.setFitHeight(100);
    }

    private void startEvent(int index) {
        loadedEvent = gameData.getCurrentRoom().getEvents().get(index);
        if(loadedEvent.getType().equals("shop")){
            loadShop(loadedEvent);
            shopMenu.setVisible(true);
        }
        else if(loadedEvent.getType().equals("battle")){
            startBattle();
        }
    }

    private void loadShop(Event shop) {
        shopItemsArea.getChildren().clear();
        for(int i=0;i<shop.getStock().size();i++){
            int index = i;
            HBox itemButton = new HBox();
            populateShopItemButton(itemButton,i);
            itemButton.setOnMousePressed(e->selectItemFromShop(index));
            shopItemsArea.getChildren().add(itemButton);
        }

    }

    private void populateShopItemButton(HBox button, int index) {
        ImageView buttonGraphic = new ImageView(new Image(loadedEvent.getStock().get(index).getImagePath()));
        buttonGraphic.setFitHeight(20);
        buttonGraphic.setFitWidth(20);
        Label itemName = new Label(loadedEvent.getStock().get(index).getName());
        Label price = new Label("" + loadedEvent.getStock().get(index).getDisplayCost());
        button.getChildren().addAll(buttonGraphic,itemName,price);
        button.setSpacing(3);
    }

    private void selectItemFromShop(int index) {
        setAllItemInfoVisible();
        ImageView itemDisplay = (ImageView)shopMenu.getChildren().get(2);
        Label nameDisplay = (Label)shopMenu.getChildren().get(3);
        Label cost = (Label)shopMenu.getChildren().get(4);
        Label buyButton = (Label)shopMenu.getChildren().get(5);
        nameDisplay.setText(loadedEvent.getStock().get(index).getName());
        cost.setText(loadedEvent.getStock().get(index).getDisplayCost());
        itemDisplay.setImage(new Image(loadedEvent.getStock().get(index).getImagePath()));
        buyButton.setOnMousePressed(e->buyItem(index));

    }
    private void selectItemFromInventory(int index) {
        setAllInventoryItemInfoVisible();
        ImageView itemDisplay = (ImageView)itemsMenu.getChildren().get(1);
        Label nameDisplay = (Label)itemsMenu.getChildren().get(2);
        Label quickSummary = (Label)itemsMenu.getChildren().get(3);
        Label useButton = (Label)itemsMenu.getChildren().get(4);
        nameDisplay.setText(gameData.getInventory().get(index).getName());
        quickSummary.setText(gameData.getInventory().get(index).getQuickSummary());
        itemDisplay.setImage(new Image(gameData.getInventory().get(index).getImagePath()));
        useButton.setOnMousePressed(e->useItem(index));

    }
    private void setAllInventoryItemInfoVisible() {
        for(int i = 0; i<itemsMenu.getChildren().size();i++){
            itemsMenu.getChildren().get(i).setVisible(true);
        }
    }

    private void setAllItemInfoVisible() {
        for(int i = 0; i<shopMenu.getChildren().size();i++){
            shopMenu.getChildren().get(i).setVisible(true);
        }
    }

    private void useItem(int index){
        gameData.getInventory().get(index).activate(getFromProperList());
        gameData.getInventory().remove(index);
        inventoryItemDisplay.setImage(null);
        inventoryNameDisplay.setText("");
        inventoryQuickSummaryDisplay.setText("");
        closeItemsMenu();

    }
    private void buyItem(int index) {
        if(gameData.getWallet().getRawAmount()>loadedEvent.getStock().get(index).getBuyPrice()){
            gameData.getInventory().add(loadedEvent.getStock().get(index));
            gameData.getWallet().spend(loadedEvent.getStock().get(index).getBuyPrice(),"B");
            updateWallet();
        }

    }

    private void updateWallet() {
        walletDisplay.setText(gameData.getWallet().getDisplayAmount());
    }

    private void setBackground() {
        String backgroundImageURL = gameData.getCurrentRoom().getImageURL();
        BackgroundImage roomBack = new BackgroundImage(new Image( backgroundImageURL,900,500,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
        backgroundImage.setBackground(new Background(roomBack));
        backgroundImage.toBack();

    }

    private void setDirectionButtonsVisible() {
        ArrayList<Button> directionButtons = new ArrayList<>();
        directionButtons.add(north);
        directionButtons.add(south);
        directionButtons.add(east);
        directionButtons.add(west);
        Map<Integer, Boolean> directions = game.checkAvailableDirections();
        for (int i = 0; i<directionButtons.size();i++){
            directionButtons.get(i).setVisible(directions.get(i));
        }

    }

    private void startBattle(){
        game.createBattle();
    }

    private void openTravel() {
        travelPane.setVisible(true);
    }

    public void closeTravel() {
        travelPane.setVisible(false);
    }


    public void travelNorth() {
        game.travelNorth();
        updateRoom();
    }

    public void travelSouth() {
        game.travelSouth();
        updateRoom();
    }

    public void travelEast() {
        game.travelEast();
        updateRoom();
    }

    public void travelWest() {
        game.travelWest();
        updateRoom();
    }

    public void saveGame() throws IOException {
        game.saveGame();
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        game.loadGame();
        initialize(this.game);
    }

    public void hideLoadSaveMenu() {
        loadSaveMenu.setVisible(false);
    }

    public void hideInspectMenu() {
        inspectMenu.setVisible(false);
    }

    public void hideShop() {
        shopMenu.setVisible(false);
        shopMenu.getChildren().get(2).setVisible(false);
        shopMenu.getChildren().get(3).setVisible(false);
        shopMenu.getChildren().get(4).setVisible(false);
        shopMenu.getChildren().get(5).setVisible(false);
    }

    public void hideTeamMenu() {
        movingFromTeam = false;
        movingFromStandby = false;
        teamMenu.setVisible(false);
    }

    public void openItemsMenu() {
        inventoryItemsArea.getChildren().clear();
        itemsMenu.setVisible(true);
        transferButton.setVisible(false);
        closeItemsButton.setVisible(true);
        openItemsButton.setVisible(false);
        for(int i=0;i<gameData.getInventory().size();i++){
            int index = i;
            HBox itemButton = new HBox();
            populateItemsButton(itemButton,i);
            itemButton.setOnMousePressed(e->selectItemFromInventory(index));
            inventoryItemsArea.getChildren().add(itemButton);
        }


    }

    private void populateItemsButton(HBox button, int index){
        ImageView buttonGraphic = new ImageView(new Image(gameData.getInventory().get(index).getImagePath()));
        buttonGraphic.setFitHeight(30);
        buttonGraphic.setFitWidth(30);
        button.getChildren().addAll(buttonGraphic);
        button.setSpacing(3);

    }

    public void closeItemsMenu() {

        itemsMenu.setVisible(false);
        transferButton.setVisible(true);
        openItemsButton.setVisible(true);
        closeItemsButton.setVisible(false);

    }
}
