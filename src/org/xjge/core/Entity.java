package org.xjge.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created: May 7, 2021
 * <p>
 * Used to represent a distinct object in the scene, typically composed of 
 * several {@link EntityComponent components} which define its behavior.
 * <p>
 * This class serves as the foundation of the engines entity component system 
 * (or ECS). Entity objects themselves are essentially just containers that gain
 * functionality through their attached components.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Entity {
    
    public final UUID uuid;
    
    Scene currentScene;
    
    private final Map<Class<? extends EntityComponent>, EntityComponent> components = new HashMap<>();
    
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
    
    Collection<EntityComponent> getAllComponents() {
        return Collections.unmodifiableCollection(components.values());
    }
    
    /**
     * Attaches a component to this entity.
     * 
     * @param <C> any subclass type that extends {@link EntityComponent}
     * @param component the subclass object representing the component to add
     * @return the entity object that the component is being attached to
     */
    public final <C extends EntityComponent> Entity addComponent(C component) {
        components.put(component.getClass(), component);
        if(currentScene != null) currentScene.queueComponentAddRequest(this, component.getClass());
        return this;
    }
    
    /**
     * Removes a component from this entity.
     * 
     * @param <C> any subclass type that extends {@link EntityComponent}
     * @param type the subclass object representing the component to remove
     * @return the removed component instance, this is useful if you want to reattach it later
     */
    public final <C extends EntityComponent> C removeComponent(Class<C> type) {
        C removed = (C) components.remove(type);
        if(removed != null && currentScene != null) currentScene.queueComponentRemoveRequest(this, type);
        return removed;
    }
    
    /**
     * Requests the removal of this entity from the current scene. This request
     * will be processed during the next game tick following this method call.
     */
    public final void removeFromScene() {
        if(currentScene != null) currentScene.queueEntityRemoveRequest(this);
    }
    
    /**
     * Used to indicate whether or not the entity has requested removal from the 
     * scene. The value of this will be reset upon adding the entity to a scene 
     * again.
     * 
     * @return true if the entity has requested to be removed from the scene
     */
    public final boolean removedFromScene() {
        return currentScene == null;
    }
    
    /**
     * Checks to see if this entity has the specified component attached.
     * 
     * @param component the subclass object representing the component to check for
     * @return true, if the entity has a component of the specified type
     */
    public final boolean hasComponent(Class<? extends EntityComponent> component) {
        return components.containsKey(component);
    }
    
    /**
     * Retrieves a component of the specified type from this entity.
     * 
     * @param <C> any subclass type that extends {@link EntityComponent}
     * @param component the subclass object representing the component to retrieve
     * @return the component instance of the specified type or <b>null</b> if no
     *         such component is currently attached
     */
    public final <C extends EntityComponent> C getComponent(Class<C> component) {
        return component.cast(components.get(component));
    }
    
}