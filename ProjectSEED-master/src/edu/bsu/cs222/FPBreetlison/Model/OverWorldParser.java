package edu.bsu.cs222.FPBreetlison.Model;

import edu.bsu.cs222.FPBreetlison.Model.Objects.Event;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Item;
import edu.bsu.cs222.FPBreetlison.Model.Objects.Room;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OverWorldParser {

    private Document document;

    OverWorldParser(){
        try {
            createDoc();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void createDoc() throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("database/GameInfo.xml");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.document = builder.parse(inputStream);
    }

    Map<String, Room> parseRoomInfo(GameData gameData) throws ParserConfigurationException, IOException, SAXException {
        createDoc();
        return createRoomMap(gameData);
    }

    private Map<String, Room> createRoomMap(GameData gameData){
        Map<String, Room> rooms = new HashMap<>();
        NodeList nodeList = this.document.getElementsByTagName("room");
        for(int i = 0; i< nodeList.getLength(); i++){
            Node roomInfo = nodeList.item(i);
            Element roomEle = (Element)roomInfo;
            Room room = new Room(createRoomString(roomEle));
            room.loadEvents(gameData);
            rooms.put(room.getName(),room);
        }
        return rooms;
    }

    private String createRoomString(Element roomElement) {
        return (roomElement.getAttribute("name") + "," +
                roomElement.getAttribute("des") + "," +
                roomElement.getAttribute("events") + ","+
                roomElement.getAttribute("north") + "," +
                roomElement.getAttribute("south") + "," +
                roomElement.getAttribute("east") + "," +
                roomElement.getAttribute("west") + "," +
                roomElement.getAttribute("image") + "," +
                roomElement.getAttribute("battleImage"));

    }

    HashMap<String, Item> createItemDatabase(){
        HashMap<String, Item> items = new HashMap<>();
        NodeList nodeList = document.getElementsByTagName("item");
        for(int i = 0; i< nodeList.getLength(); i++){
            Node itemInfo = nodeList.item(i);
            Element sourceItem = (Element)itemInfo;
            Item item = new Item(createItemString(sourceItem));
            items.put(item.getName(),item);
        }
        return items;
    }

    HashMap<String,Event> createEventDatabase(GameData gameData){
        HashMap<String, Event> events = new HashMap<>();
        NodeList nodeList = document.getElementsByTagName("event");
        for(int i = 0; i<nodeList.getLength();i++){
            Node eventInfo = nodeList.item(i);
            Element sourceEvent = (Element)eventInfo;
            Event event = new Event(createEventString(sourceEvent),gameData);
            events.put(event.getName(),event);
        }
        return events;

    }

    private String createEventString(Element sourceEvent) {
        return (sourceEvent.getAttribute("name") + "," +
                sourceEvent.getAttribute("type") + "," +
                sourceEvent.getAttribute("stock") + "," +
                sourceEvent.getAttribute("sell") + "," +
                sourceEvent.getAttribute("cluster") + "," +
                sourceEvent.getAttribute("imagePath")
        );
    }

    private String createItemString(Element sourceItem) {
        return (sourceItem.getAttribute("name") + ",") +
                sourceItem.getAttribute("description") + "," +
                sourceItem.getAttribute("quickSummary") + "," +
                sourceItem.getAttribute("affectAmt") + "," +
                sourceItem.getAttribute("buyPrice") + "," +
                sourceItem.getAttribute("type") + "," +
                sourceItem.getAttribute("type2") + "," +
                sourceItem.getAttribute("imagePath") + ",";
    }

}
