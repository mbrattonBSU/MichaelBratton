package edu.bsu.cs222.FPBreetlison.Model.Objects;

import edu.bsu.cs222.FPBreetlison.Model.GameData;

import java.util.*;



public class Event implements java.io.Serializable {

    private String name;
    private String type;
    private ArrayList<Item> stock;
    private ArrayList<String> enemyPool;
    private String imagePath;

    private GameData gameData;

    public Event(String eventInfo,GameData gameData){
        this.gameData = gameData;
        List<String> info = stringParser(eventInfo);
        this.name = info.get(0);
        this.type = info.get(1);
        String stockString = info.get(2);
        this.stock = buildStock(stockString);
        this.imagePath = info.get(5);
    }

    private List<String> stringParser(String info){
        return Arrays.asList(info.split(","));
    }

    private ArrayList<Item> buildStock(String stockString){
        List<String> stockList = Arrays.asList(stockString.split("/"));
        ArrayList<Item> builtStock = new ArrayList<>();
        for (String aStockList : stockList) {
            builtStock.add(gameData.getAllItems().get(aStockList));
        }
        return builtStock;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public ArrayList<Item> getStock() {
        return stock;
    }
    public String getImagePath() {
        return imagePath;
    }
}
