package me.about.event4j;

public class Event <T> {

    private T data;
    
    private String eventType;
    
    public Event() {}
    
    public Event(String eventType,T data) {
        this.eventType = eventType;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
}
