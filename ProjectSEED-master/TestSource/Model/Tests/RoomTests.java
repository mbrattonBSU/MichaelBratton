package Model.Tests;


import edu.bsu.cs222.FPBreetlison.Model.BattleParser;
import edu.bsu.cs222.FPBreetlison.Model.GameData;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Fighter;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Item;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Room;
import edu.bsu.cs222.FPBreetlison.Model.OverWorldParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class RoomTests {

    private Map<String, Room> allRooms;
    private HashMap<String,Fighter> allHeroes;
    private HashMap<String,Item> allItems;
    private OverWorldParser roomParser;
    private BattleParser battleParser;
    private Room testRoom;
    private GameData gameData = new GameData();


    public RoomTests() throws ParserConfigurationException, SAXException, IOException {
        allRooms = gameData.getAllRooms();
        allItems = gameData.getAllItems();
        allHeroes = gameData.getAllHeroes();

    }

    @Before
    public void setUp(){
        testRoom = gameData.getAllRooms().get("Colossal Plains");
    }

    @Test
    public void testNorth(){
        Assert.assertEquals("Luminous Caves", testRoom.getNorth());
    }

    @Test
    public void testSouth(){
        Assert.assertEquals("Inverted Forest", testRoom.getSouth());
    }

    @Test
    public void testInvalidDirection(){
        Assert.assertEquals("null", testRoom.getEast());
    }
    @Test
    public void getName(){
        Assert.assertEquals("Colossal Plains",testRoom.getName());
    }

    @Test
    public void TestItemXMLParse(){
        Item testItem = allItems.get("Patch");
        Assert.assertEquals("Patch",testItem.getName());
    }

    @Test
    public void TestHealingItem(){
        Item healItem = allItems.get("Patch");

    }

    @Test
    public void TestBuffItem(){

    }

    @Test
    public void TestEventXMLParse(){

    }
}


