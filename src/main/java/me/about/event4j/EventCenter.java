package me.about.event4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventCenter {

    private Map<String,List<EventHandler>> regTable = new HashMap<String,List<EventHandler>>();
    
    private static EventCenter _instance = new EventCenter();

    private EventCenter() {}

    public static EventCenter getInstance() {
        return _instance;
    }
    
    
    
    

}
