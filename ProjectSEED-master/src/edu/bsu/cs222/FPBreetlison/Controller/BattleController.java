package edu.bsu.cs222.FPBreetlison.Controller;

import edu.bsu.cs222.FPBreetlison.Model.Animator;
import edu.bsu.cs222.FPBreetlison.Model.BattleLogic;
import edu.bsu.cs222.FPBreetlison.Model.GameData;
import edu.bsu.cs222.FPBreetlison.Model.GameLogic;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Fighter;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Item;

import edu.bsu.cs222.FPBreetlison.Model.Objects.Skill;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Snapshot;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Shear;
import javafx.util.Duration;

import java.util.ArrayList;

public class BattleController {
    public VBox skillSelectorArea;
    public Group skillInfoDisplay;
    public Group damageDisplayArea;

    //region Variables

    private ArrayList<Fighter> team;
    private ArrayList<Fighter> enemyTeam;
    private ArrayList<Item> inventory;
    private ArrayList<Snapshot> targets;

    public VBox heroSelectorArea;
    public HBox heroGraphicsArea;
    public VBox actionMenu;
    public HBox enemySelectorArea;
    public VBox itemSelectorArea;
    public Label mainDisplay;
    public Label historyDisplay;
    public Label damageDisplay;
    public ProgressBar tpBar;
    public Label tpDisplay;
    public HBox backgroundImage;
    public ScrollPane selectorMenu;
    public Pane loaderScreen;
    public StackPane battleDisplay;
    public ImageView backButton;
    public Group itemInfoDisplay;
    public Group battlerInfoDisplay;

    public int selectedUser;
    public int selectedTarget;
    private int selectedItem;
    public boolean uiLocked;

    private GameData gameData;
    private GameLogic game;
    private BattleLogic battleLogic;
    private Animator animator;

    private Font darwinFont;
    private boolean usingSkill;


    //endregion
    //region UI Animation

    public void queueMessages(ArrayList<String> messages){
        Timeline timeline = new Timeline();
        timeline.setOnFinished(e -> clearMessages(messages));
        int dur = 80;
        for (String message : messages) {
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(dur),
                    ae -> pushMessage(message)));
            dur += 1000;
        }
        uiLocked = true;
        timeline.play();
    }

    public void queueBarUpdates(ArrayList<Snapshot> targets){
        this.targets = targets;
        if(this.targets.size() != 0){
            Timeline timeline = new Timeline();
            timeline.setOnFinished(e -> clearBarInfo(targets));
            int dur = 80;
            for (int i = 0; i<targets.size();i++) {
                int index = i;
                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(dur),
                        ae -> convertSnapshot(index)));
                timeline.getKeyFrames().add(new KeyFrame(
                        Duration.millis(dur),
                        ae -> updateHeroBars(targets.get(index))));
                dur += 1000;
            }
            timeline.play();
        }

    }

    private void convertSnapshot(int index){
        selectedUser =  targets.get(index).getIndex();
        selectedTarget = targets.get(index).getAttackerIndex();

        String anim = targets.get(index).getAnimType();
        handleAnimation(anim);
    }

    private void clearMessages(ArrayList<String> messages){
        messages.clear();
        uiLocked = false;
    }
    private void clearBarInfo(ArrayList<Snapshot> barInfos){
        barInfos.clear();
    }

    public void pushMessage(String message) {
        historyDisplay.setText(mainDisplay.getText() + "\n\n" + historyDisplay.getText());
        mainDisplay.setText(message);

    }

    private void updateHeroBars(Snapshot heroSnapshot){
        StackPane selector = (StackPane)heroSelectorArea.getChildren().get(heroSnapshot.getIndex());
        ProgressBar hBar = (ProgressBar)selector.getChildren().get(1);
        hBar.setProgress(heroSnapshot.getHpPercent());
        roundHPPercent(hBar,heroSnapshot);
        updateHeroQuickInfo(heroSnapshot);
        updateColor(hBar,heroSnapshot.getHpPercent());
        if(heroSnapshot.getKOState()){
             removeHero(heroSnapshot.getIndex());
        }
    }

    private void updateHeroQuickInfo(Snapshot heroSnapshot){
        ImageView heroImage = (ImageView)heroGraphicsArea.getChildren().get(heroSnapshot.getIndex());
        if(heroImage.isHover()){
            Label nameInfo = (Label)battlerInfoDisplay.getChildren().get(1);
            Label lvlInfo = (Label)battlerInfoDisplay.getChildren().get(2);
            Label hpInfo = (Label)battlerInfoDisplay.getChildren().get(3);

            nameInfo.setText(team.get(heroSnapshot.getIndex()).getName());
            lvlInfo.setText("Lvl: "+team.get(heroSnapshot.getIndex()).getLvl());
            hpInfo.setText(heroSnapshot.getHpString());
        }
    }

    private void updateColor(ProgressBar hbar, Double hpPercent) {
        if(hpPercent < .60 && hpPercent >= .30){
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("yellow-bar");
        }
        else if(hpPercent < .30){
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("red-bar");
        }
        else{
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("green-bar");
        }
    }

    private void startLoader(){
        loaderScreen.toFront();
        loaderScreen.setStyle("-fx-background-color: black;");
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> endLoader()));
        timeline.play();

    }

    private void endLoader() {
        loaderScreen.setVisible(false);
        battleDisplay.setVisible(true);
    }
    //endregion
    //region Initialization Functions

    public void initialize(GameLogic game){
        transferBattleData(game);
        this.game = game;
        readBattleData();
        initBackButton();
        startLoader();
        loadFonts();
        setBackground();
        formatDisplayArea();
        setupBattle();
    }

    private void initBackButton() {
        backButton.setImage(new Image("/images/system/system_backButton.png"));
        backButton.setOnMouseClicked(e->goBack());
    }

    private void transferBattleData(GameLogic game) {
        this.game = game;
        this.game.setBattleControl(this);
        this.gameData = game.getGameData();
        this.battleLogic = game.getBattleLogic();
        animator = new Animator(this.game);
        battleLogic.getGameInfo(game);

    }

    private void readBattleData() {
        team = gameData.getTeam();
        enemyTeam = gameData.getEnemyTeam();
        inventory = gameData.getInventory();
    }

    private void loadFonts() {
        darwinFont = Font.loadFont(getClass().getResource("/fonts/Darwin.ttf").toExternalForm(), 10);
    }

    private void formatDisplayArea() {

        mainDisplay.setTextFill(Color.web("0x000000"));
        mainDisplay.setFont(darwinFont);
        formatTPArea();
        for(int i = 0; i < actionMenu.getChildren().size();i++){
            Label action = (Label)actionMenu.getChildren().get(i);
            action.setFont(darwinFont);
        }
    }

    private void formatTPArea() {
        tpBar.getTransforms().add(new Shear(0.95, 0));
        tpDisplay.setFont(darwinFont);
        tpDisplay.getStyleClass().add("black-text");
    }

    private void setBackground() {
        String imageURL = gameData.getCurrentRoom().getBattleImageURL();
        BackgroundImage battleBack = new BackgroundImage(new Image(imageURL,900,500,false,true), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
        backgroundImage.setBackground(new Background(battleBack));
        backgroundImage.toBack();
    }

    private void setupBattle(){
        createHeroButtons();
        createHeroGraphics();
        createEnemySelectors();
        createItemsButtons();
        pushMessage("An enemy group led by " + enemyTeam.get(0).getName()
                + " appears!");
        battleLogic.start();
    }

    private void createHeroButtons() {
        for(int i = 0; i<team.size();i++){
            StackPane hero =  new StackPane();
            hero.setId(Integer.toString(i));
            populateHeroUIElements(hero);
            formatHeroButton((Label)hero.getChildren().get(0));
            formatHeroBar((ProgressBar)hero.getChildren().get(1),i);
            hero.setOnMousePressed(event -> selectHero(hero));
            checkForKO(hero,i);
            heroSelectorArea.getChildren().add(hero);
        }
    }

    private void checkForKO(StackPane hero, int i) {
        if(team.get(i).calcHPPercentage() == 0.0){
            Label heroLabel = (Label)hero.getChildren().get(0);
            heroLabel.setTextFill(Color.web("0x333c47"));
            hero.setOnMousePressed(null);
        }
    }

    private void populateHeroUIElements(StackPane hero) {
        int index = Integer.parseInt(hero.getId());
        Label hLabel = new Label(team.get(index).getName());
        ProgressBar hbar = new ProgressBar();
        hero.getChildren().add(hLabel);
        hero.getChildren().add(hbar);
    }

    private void formatHeroButton(Label hero) {
        hero.setScaleX(3);
        hero.setScaleY(3);
        hero.setTranslateX(-30);
        hero.setMaxWidth(40);
        hero.setMinWidth(40);
        hero.setTextFill(Color.web("0xfffff1"));
        hero.setFont(darwinFont);
    }

    private void formatHeroBar(ProgressBar hBar, int index) {
        hBar.setRotate(270);
        hBar.setScaleX(.30);
        hBar.setScaleY(.80);
        hBar.setTranslateX(26);
        hBar.getStyleClass().add("healthBar");
        hBar.getStyleClass().add("green-bar");
        hBar.setProgress(team.get(index).calcHPPercentage());
        reloadBarColor(team.get(index).calcHPPercentage(),hBar);
    }

    private void reloadBarColor(Double hpPercent, ProgressBar hbar) {
        if(hpPercent < .60 && hpPercent >= .30){
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("yellow-bar");
        }
        else if(hpPercent < .30){
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("red-bar");
        }
        else{
            hbar.getStyleClass().remove(2);
            hbar.getStyleClass().add("green-bar");
        }
    }

    private void roundHPPercent(ProgressBar hbar, Snapshot heroSnapshot) {
        if(heroSnapshot.getHpPercent() < .07 && heroSnapshot.getHpPercent() > 0){
            hbar.setProgress(0.1);
        }
    }

    private void createHeroGraphics() {
        for (int i = 0; i < team.size(); i++) {
            ImageView image = new ImageView(new Image(team.get(i).getBattlerGraphicPath()));
            image.setId(Integer.toString(i));
            image.setOnMouseEntered(event -> showHeroInfo(image));
            image.setOnMouseExited(event -> hideCharacterInfo());
            formatHeroGraphic(image,i);
            heroGraphicsArea.getChildren().add(image);
        }
    }

    private void formatHeroGraphic(ImageView image, int index) {
        image.setFitHeight(team.get(index).getSizeY());
        image.setFitWidth(team.get(index).getSizeX());
        StackPane.setAlignment(image, Pos.BOTTOM_CENTER);
        if(team.get(index).calcHPPercentage() == 0.00){
            image.setOpacity(.30);
        }
    }

    private void showHeroInfo(ImageView image){
        battlerInfoDisplay.setVisible(true);
        int index = Integer.parseInt(image.getId());
        showCharacterMiniImage(index);
        showHeroUpperLabels(index);
        showHeroLowerLabels();
    }

    private void showCharacterMiniImage(int index){
        ImageView display = (ImageView)battlerInfoDisplay.getChildren().get(0);
        display.setImage(new Image(team.get(index).getMiniGraphicPath()));
        display.setFitHeight(80);
        display.setFitWidth(80);
    }

    private void showHeroUpperLabels(int index){
        Label name = (Label)battlerInfoDisplay.getChildren().get(1);
        Label lvl = (Label)battlerInfoDisplay.getChildren().get(2);
        Label hp = (Label)battlerInfoDisplay.getChildren().get(3);
        name.setText(team.get(index).getName());
        lvl.setText("Lvl: "+team.get(index).getLvl());
        hp.setText("HP: " + team.get(index).getCurrStats().get("hp") + "/" + team.get(index).getMaxHP());
    }

    private void showHeroLowerLabels(){
    }

    private void hideCharacterInfo(){
        battlerInfoDisplay.setVisible(false);
    }

    private void createEnemySelectors() {
        ArrayList<Fighter> enemyTeam = gameData.getEnemyTeam();
        for(int i = 0; i<enemyTeam.size();i++){
            ImageView enemy = new ImageView(new Image(enemyTeam.get(i).getBattlerGraphicPath()));
            enemy.setId(Integer.toString(i));
            populateEnemyUIElements(enemy);
            enemy.setOnMouseEntered(event -> showEnemyInfo(enemy));
            enemy.setOnMouseExited(event -> hideEnemyInfo());
            HBox.setHgrow(enemy,Priority.ALWAYS);
            enemySelectorArea.getChildren().add(enemy);
        }
    }

    private void populateEnemyUIElements(ImageView enemy) {
        int index = Integer.parseInt(enemy.getId());
        enemy.setFitHeight(enemyTeam.get(index).getSizeY());
        enemy.setFitWidth(enemyTeam.get(index).getSizeX());
    }

    private void selectEnemy(ImageView enemy) {
        int index = enemySelectorArea.getChildren().indexOf(enemy);
        selectedTarget = index;
        gameData.setSelectedTarget(index);
        if(usingSkill){
            triggerSkill(index);
        }
        else{
            triggerAttack();
        }
    }

    private void showEnemyInfo(ImageView enemy){
        int index = Integer.parseInt(enemy.getId());
        battlerInfoDisplay.setVisible(true);
        showEnemyMiniImage(index);
        showEnemyUpperLabels(index);
    }

    private void showEnemyMiniImage(int index){
        ImageView display = (ImageView)battlerInfoDisplay.getChildren().get(0);
        display.setImage(new Image(enemyTeam.get(index).getMiniGraphicPath()));
        display.setFitHeight(80);
        display.setFitWidth(80);
    }

    private void showEnemyUpperLabels(int index){
        Label name = (Label)battlerInfoDisplay.getChildren().get(1);
        Label lvl = (Label)battlerInfoDisplay.getChildren().get(2);
        Label hp = (Label)battlerInfoDisplay.getChildren().get(3);
        name.setText(enemyTeam.get(index).getName());
        lvl.setText("Lvl: " + enemyTeam.get(index).getLvl());
        hp.setText("HP: " + enemyTeam.get(index).getCurrStats().get("hp") + "/" + enemyTeam.get(index).getMaxHP());
    }

    private void hideEnemyInfo(){
        battlerInfoDisplay.setVisible(false);
    }

    private void createItemsButtons(){
        for(int i = 0; i<gameData.getInventory().size();i++){
            Label item = new Label(inventory.get(i).getName());
            item.setId(Integer.toString(i));
            formatItemSelector(item);
            setupMouseEventsForItem(item);
            itemSelectorArea.getChildren().add(item);
        }
    }

    private void setupMouseEventsForItem(Label item) {
        item.setOnMousePressed(event -> selectItem(item));
        item.setOnMouseEntered(event -> hoverItem(item));
        item.setOnMouseExited(event -> exitItem());
    }

    private void exitItem() {
        itemInfoDisplay.setVisible(false);
    }

    private void hoverItem(Label item) {
        int index = itemSelectorArea.getChildren().indexOf(item);
        itemInfoDisplay.setVisible(true);
        loadItemImage(index);
        loadItemDescription(index);
    }

    private void loadItemImage(int index) {
        ImageView infoGraphic = (ImageView) itemInfoDisplay.getChildren().get(0);
        infoGraphic.setImage(new Image(gameData.getInventory().get(index).getImagePath()));
        infoGraphic.setFitWidth(50);
        infoGraphic.setFitHeight(50);
    }

    private void formatItemSelector(Label item) {
        item.setScaleX(2);
        item.setScaleY(2);
        item.setMaxWidth(60);
        item.setMinWidth(40);
        item.setTextFill(Color.web("0xffffff"));
        item.setFont(darwinFont);
    }

    private void loadItemDescription(int index) {
        Label quickSummary = (Label) itemInfoDisplay.getChildren().get(1);
        Label infoText = (Label) itemInfoDisplay.getChildren().get(2);
        quickSummary.setText(gameData.getInventory().get(index).getQuickSummary());
        infoText.setText(gameData.getInventory().get(index).getDescription());

    }
    //endregion
    //region Button Logic
    public void selectAttack() {
        if(!uiLocked){
            usingSkill = false;
            pushMessage("Who will " + team.get(selectedUser).getName() + " attack?");
            unblockEnemySelectors();
        }
    }

    public void selectEndTurn() {
        if(!uiLocked){
            battleLogic.prepareEndPlayerTurn();
            animator.backButtonSlideIn();
        }
    }

    public void selectSkills() {
        if(!uiLocked){
            populateSkills();
            skillSelectorArea.setVisible(true);
            actionMenu.setVisible(false);
        }
    }

    private void populateSkills() {
        skillSelectorArea.getChildren().clear();
        Fighter user = team.get(selectedUser);
        for (int i = 0; i<user.getSkillList().size();i++){
            Label skill = new Label(user.getSkillList().get(i).getName());
            formatSkill(skill);
            skill.setOnMouseEntered(event -> showSkillInfo(skill));
            skill.setOnMouseExited(event -> hideSkillInfo());
            skill.setOnMousePressed(event -> selectSkill(skill));
            skillSelectorArea.getChildren().add(skill);
        }
    }

    private void selectSkill(Label skillLabel) {
        if(!uiLocked){
            Fighter user = team.get(selectedUser);
            usingSkill = true;
            int index = skillSelectorArea.getChildren().indexOf(skillLabel);
            Skill skill = team.get(selectedUser).getSkillList().get(index);
            user.setQueuedSkill(skill);
            checkSkillType(skill);
        }
    }

    private void checkSkillType(Skill skill) {
        Fighter user = team.get(selectedUser);
        if(skill.getType().equals("buff")){
            battleLogic.tryActivateSkill(user,user);
        }
        else{
            pushMessage("Who will " + user.getName() + " use this skill on?");
            unblockEnemySelectors();
        }

    }

    private void hideSkillInfo() {
        skillInfoDisplay.setVisible(false);
    }

    private void formatSkill(Label skill) {
        skill.setScaleX(2);
        skill.setScaleY(2);
        skill.setTranslateX(-8);
        skill.setMaxWidth(60);
        skill.setMinWidth(40);
        skill.setWrapText(true);
        skill.setTextFill(Color.web("0xfffff1"));
        skill.setFont(darwinFont);
    }

    private void showSkillInfo(Label skill) {
        int index = skillSelectorArea.getChildren().indexOf(skill);
        Label skillQuickInfo = (Label)skillInfoDisplay.getChildren().get(1);
        skillQuickInfo.setText(team.get(selectedUser).getSkillList().get(index).getQuickInfo());
        skillInfoDisplay.setVisible(true);
    }

    public void selectBag() {
        if(!uiLocked){
            pushMessage("Which item will " + team.get(selectedUser).getName() +
                    " use?");
            itemSelectorArea.setVisible(true);
            actionMenu.setVisible(false);
        }
    }

    private void selectItem(Label item){
        selectedItem = itemSelectorArea.getChildren().indexOf(item);
        updateInventoryUI();
        battleLogic.useItem(selectedItem);
        updateSingleHeroBar();
        animator.backButtonSlideIn();
    }

    private void updateSingleHeroBar() {
        StackPane hero = (StackPane)heroSelectorArea.getChildren().get(selectedUser);
        ProgressBar heroBar = (ProgressBar)hero.getChildren().get(1);
        Fighter user = team.get(selectedUser);
        Double hpPercentage = user.calcHPPercentage();
        heroBar.setProgress(hpPercentage);
        updateColor(heroBar,hpPercentage);
    }

    private void updateInventoryUI(){
        pushMessage(team.get(selectedUser).getName() +  " used the " + gameData.getInventory().get(selectedItem).getName() + " !");
        itemSelectorArea.getChildren().remove(selectedItem);
        itemSelectorArea.setVisible(false);
        heroSelectorArea.setVisible(true);
    }

    public void selectFlee() {
        battleLogic.getMessageQueue().add("You ran away. Everyone is disappointed");
        animator.heroFlee();
        battleLogic.endBattle();
    }


    private void selectHero(StackPane hero) {
        if(!uiLocked){
            selectedUser = Integer.parseInt(hero.getId());
            heroSelectorArea.setVisible(false);
            showActionMenu();
        }
    }


    private void triggerSkill(int index){
        Fighter user = team.get(selectedUser);
        Fighter target = enemyTeam.get(index);
        battleLogic.tryActivateSkill(user,target);
    }

    private void triggerAttack() {
        battleLogic.tryHeroBasicAttack();
    }

    public void handleAnimation(String animationType){
        animator.playAnimation(animationType);
    }

    public void updateTP(){
        double percentage = (double)gameData.getCurrentTp()/(double)gameData.getTempMaxTP();
        tpBar.setProgress(percentage);
        tpDisplay.setText("TP: " + gameData.getCurrentTp() + "/" + gameData.getTempMaxTP());
    }

    public void blockEnemySelectors(){
        for(int i = 0; i<enemySelectorArea.getChildren().size();i++){
            ImageView selector = (ImageView)enemySelectorArea.getChildren().get(i);
            selector.setOnMousePressed(null);
        }
    }

    private void unblockEnemySelectors(){
        for(int i = 0; i<enemySelectorArea.getChildren().size();i++){
            ImageView selector = (ImageView)enemySelectorArea.getChildren().get(i);
            selector.setOnMousePressed(event -> selectEnemy(selector));
        }
    }

    private void showActionMenu() {
        actionMenu.setVisible(true);
        animator.backButtonSlideOut();
        pushMessage("What will " + team.get(selectedUser).getName() + " do?" );
    }

    private void removeHero(int index){
        StackPane hero = (StackPane) heroSelectorArea.getChildren().get(index);
        Label heroLabel = (Label)hero.getChildren().get(0);
        heroLabel.setTextFill(Color.web("0x333c47"));
        hero.setOnMousePressed(null);
        heroGraphicsArea.getChildren().get(index).setOpacity(.35);
        battleLogic.getMessageQueue().add(team.get(index).getName() + " is down!");
    }

    private void goBack() {
        selectorMenu.setVvalue(0);
        if(actionMenu.isVisible()){
            heroSelectorArea.setVisible(true);
            actionMenu.setVisible(false);
            blockEnemySelectors();
            animator.backButtonSlideIn();
        }
        else if(itemSelectorArea.isVisible()){
            actionMenu.setVisible(true);
            itemSelectorArea.setVisible(false);
        }
        else if(skillSelectorArea.isVisible()){
            actionMenu.setVisible(true);
            skillSelectorArea.setVisible(false);
        }
    }

    public void updateEnemyQuickInfo(Snapshot enemyState) {
        if(enemySelectorArea.getChildren().get(enemyState.getIndex()).isHover()){
            Fighter enemy = enemyTeam.get(enemyState.getIndex());
            Label enemyHP = (Label)battlerInfoDisplay.getChildren().get(3);
            enemyHP.setText("HP: " + enemy.getCurrStats().get("hp") + "/" + enemy.getMaxHP());
        }
    }

    //endregion

    public GameData getGameData(){
        return gameData;
    }


}
