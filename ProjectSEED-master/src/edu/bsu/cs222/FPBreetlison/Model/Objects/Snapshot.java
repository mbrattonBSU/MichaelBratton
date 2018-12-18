package edu.bsu.cs222.FPBreetlison.Model.Objects;

public class Snapshot implements java.io.Serializable {

    //This class is responsible for recalling information about a character at a given time
    //used in conjunction with timelines to allow for incremental updating of the UI

    private int index;
    private double hpPercent;
    private boolean KOState;
    private int attackerIndex;
    private String animType;
    private String hpString;

    public void calcHPPercent(Fighter fighter){
        int hp = fighter.getCurrStats().get("hp");
        int maxHP = fighter.getHp();
        hpPercent = (double)hp/(double)maxHP;
        hpString = "HP: " + hp + "/" + maxHP;

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getHpPercent() {
        return hpPercent;
    }

    public String getHpString(){
        return hpString;
    }

    public boolean getKOState() {
        return KOState;
    }

    public void setKOState(boolean KOState) {
        this.KOState = KOState;
    }
    public int getAttackerIndex() {
        return attackerIndex;
    }
    public void setAttackerIndex(int attackerIndex) {
        this.attackerIndex = attackerIndex;
    }

    public String getAnimType() {
        return animType;
    }

    public void setAnimType() {
        this.animType = "enemyLunge";
    }

    public int getDamage() {
        return 0;
    }
}
