package org.xjge.core;

/**
 * Created: Apr 8, 2025
 * <p>
 * Base class used to define the behavior of an {@linkplain Entity} object.
 * <p>
 * Components are intended to be narrow in scope and only contain the minimum 
 * amount of data and logic necessary to complete a single task. Each component
 * maintains a reference to its owning entity, or null if none has been assigned.
 * <p>
 * Subclasses of this type should extend this class to implement specific 
 * behaviors or properties.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public abstract class Component {

    /**
     * The current owner of this component or null if none has been assigned.
     */
    protected Entity owner;
    
}