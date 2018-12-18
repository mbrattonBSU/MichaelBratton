package edu.bsu.cs222.FPBreetlison.Model;

import edu.bsu.cs222.FPBreetlison.Model.Objects.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GameData implements java.io.Serializable {

    private ArrayList<Fighter> team;
    private ArrayList<Fighter> standby;
    private ArrayList<Fighter> enemyTeam;
    private Wallet wallet;

    private HashMap<String,Fighter> allHeroes;
    private HashMap<String,Fighter> allEnemies;
    private HashMap<String,Item> allItems;
    private HashMap<String,Event> allEvents;
    private ArrayList<Item> inventory;
    private Map<String, Room> allRooms;
    private Room currentRoom;
    private int tp;
    private int tempMaxTP;
    private int maxTP;
    private int selectedTarget;


    public GameData() throws IOException, SAXException, ParserConfigurationException {
        init();
    }

    private void init() throws ParserConfigurationException, SAXException, IOException {
        initLists();
        loadData();
        addHeroes();
        initItems();
        calcTP();
        wallet = new Wallet();

    }

    private void loadData() throws ParserConfigurationException, SAXException, IOException {
        OverWorldParser overworldLoader = new OverWorldParser();
        BattleParser battleLoader = new BattleParser();
        loadItems(overworldLoader);
        loadSkills();
        loadFighters(battleLoader);
        loadEvents(overworldLoader);
        loadRooms(overworldLoader);
    }

    public void calcTP(){
        maxTP = 0;
        for (Fighter aTeam : team) {
            maxTP += aTeam.getAgility();
        }
        tp = maxTP;
        tempMaxTP = maxTP;
    }

    void subtractTP(int amount){
        tp -= amount;
    }

    void tempIncreaseMaxTP(int amount){
        tempMaxTP+=amount;
    }

    void revertMaxTP(){
        tempMaxTP=maxTP;
    }

    private void loadItems(OverWorldParser loader) {
        allItems = loader.createItemDatabase();
    }

    private void loadEvents(OverWorldParser loader){
        allEvents = loader.createEventDatabase(this);
    }

    private void loadSkills() {
    }

    private void initLists() {
        team = new ArrayList<>();
        standby = new ArrayList<>();
        allItems = new HashMap<>();
        allEvents = new HashMap<>();
        inventory = new ArrayList<>();
        enemyTeam = new ArrayList<>();
    }

    private void loadRooms(OverWorldParser loader) throws IOException, SAXException, ParserConfigurationException {
        allRooms = loader.parseRoomInfo(this);
    }

    private void loadFighters(BattleParser loader) throws IOException, SAXException, ParserConfigurationException {
        loader.parseBattleData();
        allHeroes = loader.getHeroes();
        allEnemies = loader.getEnemies();
    }

    private void addHeroes(){
        team.add(allHeroes.get("Roxy"));
        team.add(allHeroes.get("Smitty"));
        standby.add(allHeroes.get("Blake"));
    }

    void addEnemies(){
        enemyTeam.clear();
        switch (currentRoom.getName()) {
            case "Colossal Plains":
                enemyTeam.add(new Fighter(allEnemies.get("Skraw")));
                enemyTeam.add(new Fighter(allEnemies.get("Jag")));
                break;
            case "Luminous Caves":
                enemyTeam.add(new Fighter(allEnemies.get("Harshmallow")));
                enemyTeam.add(new Fighter(allEnemies.get("Oculith")));
                break;
            case "Forest Clearing":
                enemyTeam.add(new Fighter(allEnemies.get("Blisterbulb")));
                enemyTeam.add(new Fighter(allEnemies.get("Eaflay")));
                break;
            case "Conchbreak Key":
                enemyTeam.add(new Fighter(allEnemies.get("Apparacean")));
                enemyTeam.add(new Fighter(allEnemies.get("Apparacean")));
                break;
            default:
                enemyTeam.add(new Fighter(allEnemies.get("Jag")));
                break;
        }
    }

    void removeObjectFromInventory(int index){
        inventory.remove(index);
    }

    private void initItems() {
        inventory.add(allItems.get("Patch"));
    }

    void resetHeroTP(){
        tp = tempMaxTP;
    }

    public Map<String, Room> getAllRooms(){return allRooms;}
    public Room getCurrentRoom() {
        return currentRoom;
    }
    void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }
    public ArrayList<Fighter> getTeam() {
        return team;
    }
    public ArrayList<Fighter> getStandby() {
        return standby;
    }
    public ArrayList<Fighter> getEnemyTeam() {
        return enemyTeam;
    }
    public HashMap<String, Fighter> getAllHeroes() {
        return allHeroes;
    }
    public int getTempMaxTP() {
        return tempMaxTP;
    }
    public int getCurrentTp() {
        return tp;
    }
    int getSelectedTarget() {
        return selectedTarget;
    }
    public void setSelectedTarget(int selectedTarget) {
        this.selectedTarget = selectedTarget;
    }
    void subtractTp(int cost) {
        tp -= cost;
        if(tp < 0){
            tp = 0;
        }
    }
    public HashMap<String, Item> getAllItems() {
        return allItems;
    }
    public ArrayList<Item> getInventory() {
        return inventory;
    }
    public HashMap<String, Event> getAllEvents() {
        return allEvents;
    }
    public Wallet getWallet(){
        return wallet;
    }
}
