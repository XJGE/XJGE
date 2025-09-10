package org.xjge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created: May 7, 2021
 * <p>
 * Used to represent a distinct object in the scene, typically composed of 
 * several {@link Attribute attributes} which define its behavior.
 * <p>
 * This class serves as the foundation of the engines entity component system 
 * (or ECS). Entity objects themselves are essentially just containers that gain
 * functionality through their attached attributes.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Entity {

    private boolean remove;
    
    public final UUID uuid;
    
    private final Map<Class<? extends Attribute>, Attribute> attibutes = new HashMap<>();
    
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
     * Attaches a attribute to this entity.
     * 
     * @param <C> any subclass type that extends {@link Attribute}
     * @param attribute the subclass object representing the attribute to add
     */
    public final <C extends Attribute> void addAttribute(C attribute) {
        attibutes.put(attribute.getClass(), attribute);
    }
    
    /**
     * Removes a attribute from this entity.
     * 
     * @param <C> any subclass type that extends {@link Attribute}
     * @param attribute the subclass object representing the attribute to remove
     */
    public final <C extends Attribute> void removeAttribute(C attribute) {
        attibutes.remove(attribute.getClass());
    }
    
    /**
     * Requests the removal of this entity from the current scene. This request
     * will be processed during the next game tick following this method call.
     */
    public final void removeFromScene() {
        remove = true;
    }
    
    /**
     * Checks to see if this entity has the specified attribute attached.
     * 
     * @param attribute the subclass object representing the attribute to check for
     * @return true, if the entity has a attribute of the specified type
     */
    public final boolean hasAttribute(Class<? extends Attribute> attribute) {
        return attibutes.containsKey(attribute);
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
     * Retrieves a attribute of the specified type from this entity.
     * 
     * @param <C> any subclass type that extends {@link Attribute}
     * @param attribute the subclass object representing the attribute to retrieve
     * @return the attribute instance of the specified type or <b>null</b> if no
     *         such attribute is currently attached
     */
    public final <C extends Attribute> C getAttribute(Class<C> attribute) {
        return attribute.cast(attibutes.get(attribute));
    }
    
}