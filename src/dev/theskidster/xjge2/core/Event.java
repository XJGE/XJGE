package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: May 9, 2021
 */

public abstract class Event {

    private final int priority;
    
    protected boolean resolved;
    
    public Event(int priority) {
        this.priority = priority;
    }
    
    final int getPriority() {
        return priority;
    }
    
    public abstract void resolve();
    
}