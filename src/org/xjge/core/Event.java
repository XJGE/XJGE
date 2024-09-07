package org.xjge.core;

//Created: May 9, 2021

/**
 * Objects of this type represent an event such as a pause, transition, or 
 * cutscene that temporarily disrupts the normal flow of execution. While 
 * queued, events will prevent the engine from completing its normal 
 * update/render cycle until it's been resolved.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Event {

    private final int priority;
    
    protected boolean resolved;
    
    /**
     * Creates a new event that will redirect execution until resolved. Events 
     * use numbers to indicate the priority in which they are to be resolved, 
     * with lower values denoting higher priority.
     * 
     * @param priority the priority of the event
     */
    public Event(int priority) {
        this.priority = priority;
    }
    
    /**
     * Obtains the priority number of an event.
     * 
     * @return a value denoting the precedence in which this event will be 
     *         processed by the queue
     */
    final int getPriority() {
        return priority;
    }
    
    /**
     * Called continuously until the event is resolved. Provided so events can 
     * define their own terms for resolution. To resolve an event, set the 
     * {@code resolved} field to true.
     */
    public abstract void resolve();
    
}