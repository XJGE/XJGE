package org.xjge.core;

//Created: May 9, 2021

/**
 * Objects of this type represent a game or engine event (such as a pause, 
 * cutscene, or error) that temporarily disrupts the normal flow of execution. 
 * Events should be used anytime two systems need to be decoupled in time, 
 * otherwise an {@link Observable Observable} should be considered.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public abstract class Event {

    private final int priority;
    
    protected boolean resolved;
    
    /**
     * Creates a new event that will block execution until resolved. Events use 
     * numbers to indicate the precedence in which they are to be resolved, 
     * with lower values denoting higher priority. 
     * <p>
     * NOTE: Priorities 0-3 are restricted to engine use only so you should 
     * start at 4 or greater.
     * </p>
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