package edu.bsu.cs222.FPBreetlison.Model.Objects;


import java.util.*;

public class Fighter implements java.io.Serializable {

    private String name;
    private int hp;
    private int maxHP;
    private int attack;
    private int defense;
    private int tpCost;

    private double expModifier;
    private int lvl;
    private int experience;
    private int expToNextLevel;
    private boolean isLeveledUp;

    private ArrayList<Skill> skillList;
    private HashMap<String,Integer> currStats;
    private String battlerGraphicPath;
    private String miniGraphicPath;
    private Skill queuedSkill;
    private int sizeX;
    private int sizeY;
    private double rewardAmt;
    private int KOLevel;
    private int lastDamage;
    private String weakness;
    private String strength;
    private int agility;

    public Fighter(String info){

        List<String> characterInfo = stringParser(info);
        expToNextLevel=150;
        currStats = new HashMap<>();
        skillList = new ArrayList<>();
        KOLevel = 0;
        lvl = 1;
        loadInfo(characterInfo);
        associateStats();
    }

    public Fighter(Fighter fighter){
        loadInfoForCopy(fighter);
        associateStats();
    }

    private void loadInfoForCopy(Fighter fighter) {
        expToNextLevel=150;
        currStats = new HashMap<>();
        skillList = new ArrayList<>();
        KOLevel = 0;
        lvl = 1;
        this.name = fighter.getName();
        this.maxHP = fighter.getMaxHP();
        this.hp = fighter.getHp();
        this.attack = fighter.getAttack();
        this.defense = fighter.getDefense();
        this.agility = fighter.getAgility();
        this.tpCost = fighter.getTpCost();
        this.expModifier = fighter.getExpModifier();
        this.weakness = fighter.getWeakness();
        this.strength = fighter.getStrength();
        this.battlerGraphicPath = fighter.getBattlerGraphicPath();
        this.miniGraphicPath = fighter.getMiniGraphicPath();
        this.sizeX = fighter.getSizeX();
        this.sizeY = fighter.getSizeY();
        this.rewardAmt = fighter.getRewardAmt();
    }

    private void loadInfo(List<String> characterInfo) {
        this.name = characterInfo.get(0);
        this.maxHP = Integer.parseInt(characterInfo.get(1));
        this.hp = maxHP;
        this.attack = Integer.parseInt(characterInfo.get(2));
        this.defense = Integer.parseInt(characterInfo.get(3));
        this.agility = Integer.parseInt(characterInfo.get(6));
        this.tpCost = Integer.parseInt(characterInfo.get(7));
        this.expModifier = Double.parseDouble(characterInfo.get(8));
        this.weakness = characterInfo.get(9);
        this.strength = characterInfo.get(10);
        this.battlerGraphicPath = characterInfo.get(11);
        this.miniGraphicPath = characterInfo.get(12);
        this.sizeX = Integer.parseInt(characterInfo.get(13));
        this.sizeY = Integer.parseInt(characterInfo.get(14));
        this.rewardAmt = Double.parseDouble(characterInfo.get(15));
    }

    public double calcHPPercentage(){
        return (double)currStats.get(("hp"))/(double)maxHP;
    }

    private void associateStats(){
        currStats.put("hp",hp);
        currStats.put("attack",attack);
        currStats.put("defense",defense);
        currStats.put("tpCost",tpCost);
    }

    public void revertStats(){
        currStats.replace("attack",currStats.get("attack"),attack);
        currStats.replace("defense",currStats.get("defense"),defense);
    }

    //region In-Battle Functionality

    public void doBasicAttack(Fighter target){
        double damage = this.getCurrStats().get("attack")*2/target.getCurrStats().get("defense");
        int finalDamage = (int)Math.round(damage*1.5);
        if(finalDamage<1){
            finalDamage=1;
        }
        lastDamage = finalDamage;
        target.takeDamage(finalDamage);
    }

    public void addSkill(Skill skill){
        skillList.add(skill);
    }

    public void getExp(){
        experience += 200;
    }

    public void checkLevel(){
        if(experience >= expToNextLevel){
            levelUp();
        }

    }

    private void levelUp(){
        lvl +=1;
        maxHP+=5;
        attack+=2;
        defense+=2;
        int levelDifference = experience-expToNextLevel;
        double nextLevelRaw = expToNextLevel + expToNextLevel*expModifier;
        expToNextLevel = (int)Math.round(nextLevelRaw);
        experience = levelDifference;
        revertStats();
        isLeveledUp=true;

    }

    //endregion

    //region Reaction Functionality

    public void takeDamage(int damage){
        int oldHP = currStats.get("hp");
        currStats.replace("hp",oldHP,oldHP-damage);
        if(currStats.get("hp") < 0){
            oldHP = currStats.get("hp");
            currStats.replace("hp",oldHP,0);
        }

    }

    void recoverHealth(int heal){
        int hp = currStats.get("hp");
        currStats.replace("hp",currStats.get("hp"),hp+heal);
        if(currStats.get("hp") > maxHP || heal == -1){
            currStats.replace("hp",currStats.get("hp"),maxHP);
        }
    }

    void strengthenStat(String stat, int factor){
        int oldValue = currStats.get(stat);
        int newValue = factor + oldValue;
        currStats.replace(stat,oldValue,newValue);
    }

    void weakenStat(int factor){
        int oldValue = currStats.get("attack");
        int newValue = oldValue - factor;
        currStats.replace("attack",oldValue,newValue);
    }

    //endregion

    //region Text-Related Functionality

    private List<String> stringParser(String info){
        return Arrays.asList(info.split(","));
    }

    //endregion

    public int checkKOLevel(){
        if(currStats.get("hp")<=0 && KOLevel == 1){
            KOLevel = 2;
        }
        else if(currStats.get("hp")==0 && KOLevel ==0){
            KOLevel = 1;
        }
        return KOLevel;

    }
    public boolean isKO(){
        return currStats.get("hp") == 0;

    }

    //region Setters and Getters


    public ArrayList<Skill> getSkillList() {
        return skillList;
    }
    public String getName() {
        return name;
    }
    int getHp() {
        return hp;
    }
    public int getMaxHP() {
        return maxHP;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getTpCost() {
        return tpCost;
    }
    public int getKOLvl() {
        return KOLevel;
    }
    public String getBattlerGraphicPath() {
        if(battlerGraphicPath.equals("null")){
            battlerGraphicPath = "/images/system/system_undefined.png";
        }
        return battlerGraphicPath;
    }
    public String getMiniGraphicPath(){return miniGraphicPath;}
    public int getSizeX(){return sizeX;}
    public int getSizeY(){return sizeY;}
    public Skill getQueuedSkill() {
        return queuedSkill;
    }
    public void setQueuedSkill(Skill queuedSkill) {
        this.queuedSkill = queuedSkill;
    }
    public HashMap<String, Integer> getCurrStats() {
        return currStats;
    }
    public boolean isLeveledUp() {
        return isLeveledUp;
    }
    public void setLeveledUp() {
        isLeveledUp = false;
    }
    public int getLvl() {
        return lvl;
    }
    public int getLastDamage() {
        return lastDamage;
    }
    private double getExpModifier() {
        return expModifier;
    }
    public double getRewardAmt() {
        return rewardAmt;
    }
    public String getWeakness() {
        return weakness;
    }
    public int getAgility() {
        return agility;
    }

    public String getStrength() {
        return strength;
    }
    //endregion

}
