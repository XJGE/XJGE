package dev.theskidster.xjge2.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * @author J Hoffman
 * Created: May 9, 2021
 */

public class Observable {

    private final PropertyChangeSupport observable;
    public final HashMap<String, Object> properties = new HashMap<>();
    
    public Observable(Object object) {
        observable = new PropertyChangeSupport(object);
    }
    
    public void addObserver(PropertyChangeListener observer) {
        observable.addPropertyChangeListener(observer);
    }
    
    public void removeObserver(PropertyChangeListener observer) {
        observable.removePropertyChangeListener(observer);
    }
    
    public void notifyObservers(String name, Object property) {
        observable.firePropertyChange(name, properties.get(name), property);
        properties.put(name, property);
    }
    
}