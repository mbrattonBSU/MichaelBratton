package edu.bsu.cs222.FPBreetlison.Model.Objects;

import java.util.Arrays;
import java.util.List;

public class Skill implements java.io.Serializable {
    private String name;
    private int affectAmt;
    private int tpCost;
    private String type;
    private String quickInfo;
    private String extraMessage;
    private String animType;
    private String element;


    public Skill(String info){
        List<String> skillInfo = stringParser(info);
        this.name = skillInfo.get(0);
        this.affectAmt = Integer.parseInt(skillInfo.get(1));
        this.tpCost = Integer.parseInt(skillInfo.get(2));
        this.element = skillInfo.get(3);
        this.type = skillInfo.get(4);
        this.quickInfo = skillInfo.get(6);
        this.extraMessage =  skillInfo.get(7);
        this.animType = skillInfo.get(8);
    }

    public void use(Fighter user, Fighter target){
        switch (type) {
            case "heal":
                user.recoverHealth(affectAmt);
                break;
            case "attack":
                double damage = user.getCurrStats().get("attack") * affectAmt / target.getCurrStats().get("defense");
                int finalDamage = (int) Math.round(damage * 1.5 * elementModifier(target));
                if(finalDamage < 1){
                    finalDamage = 1;
                }
                target.takeDamage(finalDamage);
                break;
            case "debuff":
                target.weakenStat(affectAmt);
                break;
            case "buff":
                user.strengthenStat("attack", affectAmt);
                break;
            case "selfDebuff":

                break;
        }
    }

    private double elementModifier(Fighter target) {
        if(element.equals(target.getWeakness())){
            return 1.5;
        }
        else if(element.equals(target.getStrength())){
            return .5;
        }
        return 1;
    }

    private List<String> stringParser(String info){
        return Arrays.asList(info.split(","));
    }

    public String getName() {
        return name;
    }
    public String getQuickInfo() {
        return quickInfo;
    }
    public String getExtraMessage() {
        return extraMessage;
    }
    public String getType() {
        return type;
    }
    public int getTpCost() {
        return tpCost;
    }
    public String getAnimType() {
        return animType;
    }
    public String getElement() {
        return element;
    }
}
