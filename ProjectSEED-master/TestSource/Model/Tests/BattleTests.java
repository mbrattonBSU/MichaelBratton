package Model.Tests;


import edu.bsu.cs222.FPBreetlison.Model.BattleLogic;
import edu.bsu.cs222.FPBreetlison.Model.BattleParser;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Fighter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class BattleTests {

    private BattleParser parser;
    private Document characterInfo;

    HashMap<String,Fighter> allHeroes;
    HashMap<String,Fighter> allEnemies;
    BattleLogic battleLogic;
    private Fighter attacker;
    private Fighter defender;

    private ArrayList<Fighter> enemyTeam;
    private ArrayList<Fighter> heroTeam;


    //region Test Initialization
    @Before
    public void setUp() throws ParserConfigurationException, IOException, SAXException {
        battleLogic = new BattleLogic();
        heroTeam = new ArrayList<>();
        enemyTeam = new ArrayList<>();
        initFighters();
        initTeams();

    }

    private void initFighters() throws IOException, SAXException, ParserConfigurationException {
        attacker = new Fighter("Elmira,10,2,2,2,2,2,2,1.65,/images/system/system_undefined.png,/images/system/system_undefined.png,200,200");
        defender = new Fighter("Thompson,10,2,2,2,2,2,2,1.65,/images/system/system_undefined.png,/images/system/system_undefined.png,200,200");
        parser = new BattleParser();
        parser.parseBattleData();
        allHeroes = parser.getHeroes();
        allEnemies = parser.getEnemies();
    }

    private void initTeams() {
        heroTeam.add(new Fighter(allHeroes.get("Smitty")));
        heroTeam.add(new Fighter(allHeroes.get("Roxy")));
        enemyTeam.add(new Fighter(allEnemies.get("Jag Inf.")));
        enemyTeam.add(new Fighter(allEnemies.get("Blisterbulb")));

    }

    @Test
    public void TestKODetection(){
        defender.takeDamage(9999999);
        Assert.assertEquals(true,defender.isKO());
    }

    @Test
    public void TestDamageCalcGeneral(){
        attacker.doBasicAttack(defender);
        System.out.println(attacker.getLastDamage());
        Assert.assertEquals(3,attacker.getLastDamage());
    }

    @Test
    public void TestAttackSkill(){

    }

    @Test
    public void TestBuffSkill(){

    }

    @Test
    public void TestHealSkill(){

    }

    @Test
    public void TestFleeChecker(){

    }

    //endregion
}
