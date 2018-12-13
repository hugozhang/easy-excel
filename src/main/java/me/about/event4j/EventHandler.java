package me.about.event4j;

public interface EventHandler<T> {

    void handle (Event<T> event);
    
}
