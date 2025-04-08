package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created: Apr 8, 2025
 * 
 * Used to represent objects in the scene.
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class Entity {

    private boolean remove;
    
    public final UUID uuid;
    
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    
    Entity() {
        this(UUID.randomUUID());
    }
    
    Entity(UUID uuid) {
        this.uuid = uuid;
    }
    
    void resetRemovalRequest() {
        remove = false;
    }
    
    public final <C extends Component> void addComponent(C component) {
        component.owner = this;
        components.put(component.getClass(), component);
    }
    
    public final <C extends Component> void removeComponent(C component) {
        component.owner = null;
        components.remove(component.getClass());
    }
    
    public final void removeFromScene() {
        remove = true;
    }
    
    public final boolean hasComponent(Class<? extends Component> component) {
        return components.containsKey(component);
    }
    
    public final boolean removalRequested() {
        return remove;
    }
    
    public final <C extends Component> C getComponent(Class<C> component) {
        return component.cast(components.get(component));
    }
    
}