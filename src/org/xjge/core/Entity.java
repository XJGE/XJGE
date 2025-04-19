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
    
    /**
     * Creates a new entity object with a random UUID.
     */
    public Entity() {
        this(UUID.randomUUID());
    }
    
    /**
     * Creates a new entity object using an existing UUID.
     * 
     * @param uuid the value of the UUID that will be used to identify this entity
     */
    public Entity(UUID uuid) {
        this.uuid = uuid;
    }
    
    /**
     * Called automatically by the engine to reset the value of the remove flag 
     * in the case this entity was previously removed from the scene.
     */
    void resetRemovalRequest() {
        remove = false;
    }
    
    /**
     * Adds a component to this entity.
     * 
     * @param <C> any subclass type that extends {@linkplain Component}
     * @param component the subclass object representing the component to add, 
     *                  its owner will set to this entity 
     */
    public final <C extends Component> void addComponent(C component) {
        component.owner = this;
        components.put(component.getClass(), component);
    }
    
    /**
     * Removes a component from this entity.
     * 
     * @param <C> any subclass type that extends {@linkplain Component}
     * @param component the subclass object representing the component to remove, 
     *                  its owner will be set to null
     */
    public final <C extends Component> void removeComponent(C component) {
        component.owner = null;
        components.remove(component.getClass());
    }
    
    /**
     * Requests the removal of this entity from the current scene. This request
     * will be processed during the next game tick following this method call.
     */
    public final void removeFromScene() {
        remove = true;
    }
    
    /**
     * Checks to see if this entity has the specified component attached.
     * 
     * @param component the subclass object representing the component to check for
     * @return true, if the entity has a component of the specified type
     */
    public final boolean hasComponent(Class<? extends Component> component) {
        return components.containsKey(component);
    }
    
    /**
     * Used to indicate whether or not the entity has requested removal from the 
     * scene. The value of this will be reset upon adding the entity to a scene 
     * again.
     * 
     * @return true if the entity has requested to be removed from the scene
     */
    public final boolean removalRequested() {
        return remove;
    }
    
    /**
     * Retrieves a component of the specified type from this entity.
     * 
     * @param <C> any subclass type that extends {@linkplain Component}
     * @param component the subclass object representing the component to retrieve
     * @return the component instance of the specified type or <b>null</b> if no
     *         such component is currently attached
     */
    public final <C extends Component> C getComponent(Class<C> component) {
        return component.cast(components.get(component));
    }
    
}