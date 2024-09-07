package org.xjge.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

//Created: May 9, 2021

/**
 * A Component object which can be used to relay information about state 
 * changes occurring in the implementing object to one or more observers 
 * located anywhere in the codebase. 
 * <p>
 * Objects which make use of observable should define the properties (often 
 * fields) observers should look for in their constructors with the properties 
 * collection provided. This usually looks something like this:
 * <blockquote><pre>
 * int prop1     = 0;
 * boolean prop2 = false;
 * char prop3    = 'someVal';
 * 
 * Observable observable = new Observable(this);
 * 
 * MyObject(Object other) {
 *     ...
 *     observable.properties.put("myProperty1", prop1);
 *     observable.properties.put("myProperty2", prop2);
 *     observable.properties.put("myProperty3", prop3);
 *     ...
 *     observable.addObserver(other);
 *     ...
 * }
 * 
 * <b>//Called every game tick...</b>
 * MyObject.update() {
 *     notifyObservers("myProperty1", prop1);
 *     notifyObservers("myProperty2", prop2);
 *     notifyObservers("myProperty3", prop3);
 *     ...
 * }
 * </pre></blockquote>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Observable {

    private final PropertyChangeSupport observable;
    public final HashMap<String, Object> properties = new HashMap<>();
    
    /**
     * Creates a new observable object that will look for state changes in the 
     * object provided and supply it to other parts of the codebase.
     * 
     * @param object the implementing object that will expose its state
     */
    public Observable(Object object) {
        observable = new PropertyChangeSupport(object);
    }
    
    /**
     * Exposes this objects state to another. Observer objects must implement 
     * {@link java.beans.PropertyChangeListener PropertyChangeListener}.
     * 
     * @param observer the object that is interested in the state changes of 
     *                 the one implementing the {@link Observable} component
     */
    public void addObserver(PropertyChangeListener observer) {
        observable.addPropertyChangeListener(observer);
    }
    
    /**
     * Revokes an observers access to view the state changes of implementing 
     * objects.
     * 
     * @param observer the observer to remove
     */
    public void removeObserver(PropertyChangeListener observer) {
        observable.removePropertyChangeListener(observer);
    }
    
    /**
     * Notifies all observers of state changes in this object.
     * 
     * @param name the name of the property (field) we're observing 
     * @param property an object representing the value of the property changed
     */
    public void notifyObservers(String name, Object property) {
        observable.firePropertyChange(name, properties.get(name), property);
        properties.put(name, property);
    }
    
}