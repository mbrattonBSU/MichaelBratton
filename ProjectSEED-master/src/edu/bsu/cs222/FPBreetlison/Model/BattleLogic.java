package edu.bsu.cs222.FPBreetlison.Model;

import edu.bsu.cs222.FPBreetlison.Controller.BattleController;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Item;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Skill;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Snapshot;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Fighter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;


public class BattleLogic {

    private GameData gameData;
    private GameLogic game;
    private BattleController battleController;
    private Animator animator;

    private Fighter attacker;
    private Fighter target;

    private int targetNo;
    private Snapshot fighterSnapshot;
    private ArrayList<Snapshot> targetQueue;
    private ArrayList<String> messageQueue;

    private boolean heroWon;

    public void getGameInfo(GameLogic game){
        this.game = game;
        this.gameData = game.getGameData();
        gameData.addEnemies();
        this.battleController = game.getBattleControl();
        animator = new Animator(game);

    }

    public void start(){

        messageQueue = new ArrayList<>();
        targetQueue = new ArrayList<>();
        gameData.resetHeroTP();
        battleController.updateTP();
        checkSpeeds();
    }

    private void updateTurn(String phase) {

        switch (phase) {
            case "hero":
                enableCharacterMenu();
                break;
            case "enemy":
                disableCharacterMenu();
                tryEnemyAttack();
                resetTP();
                endEnemyTurn();
                break;
            case "enemyWin":
                battleController.blockEnemySelectors();
                battleController.uiLocked = true;
                endBattle();
                break;
            case "heroWin":
                heroWon = true;
                battleController.blockEnemySelectors();
                battleController.heroSelectorArea.setVisible(false);
                battleController.uiLocked = true;
                gainExp();
                calculateRewards();
                endBattle();
                break;
        }
        animator.backButtonSlideIn();
    }

    private void calculateRewards() {
        int rewardAmt = 0;
        for(int i = 0; i < gameData.getEnemyTeam().size();i++){
            rewardAmt+=gameData.getEnemyTeam().get(i).getRewardAmt();
        }
        gameData.getWallet().collect(rewardAmt,"B");
        messageQueue.add("You collected " + rewardAmt + " bytes!");
    }


    public void endBattle(){
        revertFighterStats();
        gameData.revertMaxTP();
        int finalQueue = messageQueue.size();
        battleController.queueMessages(messageQueue);
        int dur = 80;
        battleController.uiLocked = true;
        animator.backButtonSlideIn();
        dur += finalQueue *1000;
        Timeline counter = new Timeline();
        counter.getKeyFrames().add(new KeyFrame(
                Duration.millis(dur),
                ae -> game.setUpOverworld()));
        counter.play();
        finalQueue = 0;
        game.setBattleUnderway();
    }

    private void revertFighterStats() {
        for(int i = 0; i< gameData.getTeam().size();i++){
            gameData.getTeam().get(i).revertStats();
        }
    }

    private void resetTP() {
        gameData.resetHeroTP();
        battleController.updateTP();
    }

    private void tryEnemyAttack() {
        for (int i = 0; i< gameData.getEnemyTeam().size();i++) {
            if(!gameData.getEnemyTeam().get(i).isKO()){
                findLivingHero(i);
            }
        }
    }

    private void findLivingHero(int i) {
        fighterSnapshot = new Snapshot();
        fighterSnapshot.setAttackerIndex(i);
        fighterSnapshot.setAnimType();
        targetNo = makeRandom(gameData.getTeam().size());
        Fighter target = gameData.getTeam().get(targetNo);
        if (target.isKO()) {
            target = findNextTarget();
        }
        if (target != null) {
            fighterSnapshot.setIndex(targetNo);
            doEnemyAttack(gameData.getEnemyTeam().get(i), target);
        }
        else {
            fighterSnapshot.setIndex(targetNo);
            fighterSnapshot.setKOState(true);
            targetQueue.add(fighterSnapshot);
            updateTurn("enemyWin");
        }
    }
//
//    private ArrayList<Fighter> getRemainingEnemies() {
//        ArrayList<Fighter> fighters = gameData.getEnemyTeam();
//        ArrayList<Fighter> ableFighters = new ArrayList<>();
//        for (Fighter fighter : fighters) {
//            if (fighter.getKOLvl() == 0) {
//                ableFighters.add(fighter);
//            }
//        }
//        return ableFighters;
//    }

    private void doEnemyAttack(Fighter user, Fighter target){
        user.doBasicAttack(target);
        fighterSnapshot.calcHPPercent(target);
        detectHeroKO();
        fighterSnapshot.setKOState(target.isKO());
        messageQueue.add(user.getName() + " strikes " + target.getName() + " !");
        targetQueue.add(fighterSnapshot);

    }

    private Fighter findNextTarget() {

        for(int i = 0; i<gameData.getTeam().size();i++){
            Fighter target = gameData.getTeam().get(i);
            if(!target.isKO()){
                targetNo = i;
                return target;
            }
        }
        return null;
    }

    private void endEnemyTurn() {
        battleController.heroSelectorArea.setVisible(true);
        if(detectHeroKO()){
            updateTurn("enemyWin");
            messageQueue.add("Everyone's trashed! You lose!");
        }
        else if(!detectHeroKO() && !heroWon ){
            messageQueue.add("It's your turn!");
            updateTurn("hero");

        }
        battleController.queueMessages(messageQueue);
        battleController.queueBarUpdates(targetQueue);
        battleController.uiLocked = true;
    }

    private boolean detectHeroKO() {
        ArrayList<Fighter> fighters = gameData.getTeam();
        int KOamt = 0;

        for (Fighter fighter : fighters) {
            fighter.checkKOLevel();
            if (fighter.getKOLvl() > 0) {
                KOamt++;

            }
        }
        return KOamt == fighters.size();
    }

    private int makeRandom(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    private void checkSpeeds() {

    }

    private void enableCharacterMenu() {
        battleController.heroSelectorArea.setVisible(true);
        animator.backButtonSlideIn();
    }

    private void disableCharacterMenu(){
        battleController.heroSelectorArea.setVisible(false);
        battleController.actionMenu.setVisible(false);
    }

    private void checkPlayerTP(){
        if(gameData.getCurrentTp() <= 0){
            battleController.skillSelectorArea.setVisible(false);
            prepareEndPlayerTurn();

        }
    }

    private void enemyTurnDelay(){
        Timeline counter = new Timeline();
        counter.getKeyFrames().add(new KeyFrame(
                Duration.millis(500),null));
        counter.setOnFinished(ae->endPlayerTurn());
        counter.play();
    }

    public void tryHeroBasicAttack(){
        attacker = gameData.getTeam().get(battleController.selectedUser);
        target = gameData.getEnemyTeam().get(gameData.getSelectedTarget());
        int cost = attacker.getTpCost();
        if(gameData.getCurrentTp() >= cost ){
            battleController.handleAnimation("heroLunge");
            messageQueue.add(attacker.getName() + " attacks " + target.getName() +"!");
            battleController.queueMessages(messageQueue);
            detectEnemyKO();
            startHeroBasicAttack(cost);
        }
        else{
            battleController.pushMessage("There's not enough time left for " + attacker.getName()
                    + " to attack!");
        }

    }

    private void startHeroBasicAttack(int cost) {
        gameData.subtractTp(cost);
        attacker.doBasicAttack(target);
        battleController.handleAnimation("heroLunge");
        updateUIForHeroAttack();
        detectEnemyKO();
        battleController.queueMessages(messageQueue);
        checkPlayerTP();

    }

    private void updateUIForHeroAttack(){
        Snapshot enemyState = new Snapshot();
        enemyState.setIndex(gameData.getSelectedTarget());
        enemyState.calcHPPercent(target);
        battleController.updateEnemyQuickInfo(enemyState);
        battleController.blockEnemySelectors();
        battleController.updateTP();
    }

    private void detectEnemyKO() {
        ObservableList<Node> selectors = battleController.enemySelectorArea.getChildren();
        ArrayList<Fighter> fighters = gameData.getEnemyTeam();
        int KOamt = 0;
        for (int i = 0; i < fighters.size(); i++) {
            fighters.get(i).checkKOLevel();
            if (fighters.get(i).getKOLvl() > 0) {
                KOamt++;
            }
            if(fighters.get(i).getKOLvl() == 1){
                selectors.get(i).setVisible(false);
                messageQueue.add(fighters.get(i).getName() + " is down!");
            }
        }
        if (KOamt == fighters.size()) {
            heroWon = true;
            messageQueue.add("The enemy team is down! You won!");
            updateTurn("heroWin");

        }
    }

    public void prepareEndPlayerTurn(){
        battleController.blockEnemySelectors();
        battleController.queueMessages(messageQueue);
        battleController.uiLocked = true;
        enemyTurnDelay();
    }
    private void endPlayerTurn() {
        updateTurn("enemy");
    }

    public void useItem(int itemNo) {
        Item item = gameData.getInventory().get(itemNo);
        if(item.getType().equals("tpBuff")){
            gameData.tempIncreaseMaxTP(item.getAffectAmt());
            battleController.updateTP();
        }
        else{
            item.activate(gameData.getTeam().get(battleController.selectedUser));
        }

        gameData.removeObjectFromInventory(itemNo);
    }

    public ArrayList<String> getMessageQueue() {
        return messageQueue;
    }

    public void tryActivateSkill(Fighter user, Fighter target) {
        Skill skill = user.getQueuedSkill();
        attacker = user;
        this.target = target;
        if(gameData.getCurrentTp() >= skill.getTpCost()){
            activateSkill(user, target);
            updateUIForHeroAttack();
            detectEnemyKO();
            battleController.handleAnimation(skill.getAnimType());
            battleController.queueMessages(messageQueue);
            //The skill is not trying to activate
            checkPlayerTP();
        }
        else{
            battleController.pushMessage("There's not enough time to use that skill!");
        }


    }
    private void activateSkill(Fighter user, Fighter target){
        Skill skill = user.getQueuedSkill();
        user.getQueuedSkill().use(user, target);
        gameData.subtractTP(skill.getTpCost());
        battleController.updateTP();
        messageQueue.add(user.getName() + " used " + skill.getName() + "!");
        if(skill.getElement().equals(target.getWeakness())){
        messageQueue.add(user.getName() + " hit " + target.getName() + "'s weakness! Double Damage!");
        }
        if(skill.getElement().equals((target.getStrength()))){
            messageQueue.add(target.getName() +  " was strong against the attack. Half damage!");
        }
        extraMessage(skill);
        battleController.queueMessages(messageQueue);
    }

    private void extraMessage(Skill skill){
        if(!skill.getExtraMessage().equals("null")){
            messageQueue.add(skill.getExtraMessage());
        }

    }

    private void gainExp() {
        for(int i = 0; i<gameData.getTeam().size();i++){
            Fighter fighter = gameData.getTeam().get(i);
            fighter.getExp();
            fighter.checkLevel();
            if(fighter.isLeveledUp()){
                messageQueue.add(fighter.getName() + " leveled up!");
                fighter.setLeveledUp();
            }
        }
        battleController.queueMessages(messageQueue);

    }
}
