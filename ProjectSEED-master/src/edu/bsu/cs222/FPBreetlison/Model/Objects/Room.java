package edu.bsu.cs222.FPBreetlison.Model.Objects;

import edu.bsu.cs222.FPBreetlison.Model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room implements java.io.Serializable {

    private String name;
    private String description;
    private List<String> eventList;
    private String north;
    private String south;
    private String east;
    private String west;
    private String imagePath;
    private String battleImagePath;
    private ArrayList<Event> events;

    public Room(String info){

        List<String> roomInfo = stringParser(info);
        events = new ArrayList<>();
        this.name = roomInfo.get(0);
        this.description = roomInfo.get(1);
        eventList = eventParser(roomInfo.get(2));
        this.north = roomInfo.get(3);
        this.south = roomInfo.get(4);
        this.east = roomInfo.get(5);
        this.west = roomInfo.get(6);
        this.imagePath = roomInfo.get(7);
        this.battleImagePath = roomInfo.get(8);
    }

    public void loadEvents(GameData gameData) {
        for (String anEventList : eventList) {
            Event event = gameData.getAllEvents().get(anEventList);
            events.add(event);
        }
    }
    private List<String> stringParser(String info) {
        return Arrays.asList(info.split(","));
    }
    private List<String> eventParser(String info){
        return Arrays.asList(info.split("/"));
    }

    public String getNorth(){
        return north;
    }
    public String getSouth(){
        return south;
    }
    public String getEast(){
        return east;
    }
    public String getWest(){
        return west;
    }

    public String getImageURL(){
        if (this.imagePath.equals("null")){
        return "images/system/system_undefined.png";
        }
        else return imagePath;

    }
    public String getBattleImageURL() {
        if (this.battleImagePath.equals("null")){
            return "images/system/system_undefined.png";
        }
        else return battleImagePath;
    }
    public String getDescription(){
        if(description.equals("null")){
            return "(description)";
        }
       return description;
    }
    public String getName(){
        return name;
    }
    public ArrayList<Event> getEvents() {
        return events;
    }
}
