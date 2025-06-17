package org.xjge.core;

import org.xjge.graphics.Color;

/**
 * Created: May 11, 2021
 * <p>
 * Provides utilities for managing high-level game logic.
 * <p>
 * More specifically the game class can be used to change the current scene 
 * being rendered or the flow of execution through its access to the engines 
 * central event queue. In addition to these features it also provides some 
 * convenience methods useful for general gameplay operations.
 * <p>
 * These gameplay methods include:
 * <ul>
 * <li>{@linkplain #addEntity(int, Entity)}</li>
 * <li>{@linkplain #addEvent(Event)}</li>
 * <li>{@linkplain #addLight(int, Light)}</li>
 * <li>{@linkplain #setClearColor(Color)}</li>
 * <li>{@linkplain #setScene(Scene)}</li>
 * <li>{@linkplain #tick(int)}</li>
 * <li>{@linkplain #setBloomThreshold(float)}</li>
 * </ul>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Game {
    
    private static double deltaMetric = 0;
    
    private static boolean ticked;
    public static boolean enableBloom;
    
    private static Scene scene;
    
    /**
     * Obtains engine runtime information.
     * <p>
     * NOTE: The current delta value returned by this method is truncated and 
     * intended for display/debug purposes only. A more accurate version can be 
     * found in the {@linkplain Scene#update(double, double) update method} of
     * the current scene under the {@code trueDelta} argument.
     * 
     * @return the time (in seconds) it took the engine to complete an update cycle
     */
    static float getDelta() {
        return (float) deltaMetric;
    }
    
    /**
     * Obtains engine runtime information.
     * 
     * @return true of the game has completed a pass of its update cycle
     */
    static boolean getTicked() {
        return ticked;
    }
    
    /**
     * Obtains the display name of the current Scene being rendered.
     * 
     * @return the name of the current scene
     */
    public static String getSceneName() {
        return scene.name;
    }
    
    /**
     * Adds an entity to the current scene. Generally speaking you'll only want 
     * to use this for debugging purposes. Otherwise entity objects should be 
     * managed directly from within the scene subclass itself, or through a 
     * call to a method like 
     * {@linkplain Scene#addEntity(org.xjge.core.Entity) Scene.addEntity()}.
     * 
     * @param entity the new entity object that will be added to this scene
     */
    public static final void addEntity(Entity entity) {
        scene.entities.put(entity.uuid, entity);
    }
    
    /**
     * Inserts a light object into the current scenes 
     * {@linkplain Scene#lights lights array} at the specified index. This 
     * method is particularly useful in instances where lighting effects need 
     * to exhibit some level of dynamic behavior- such as an explosion in a 
     * dark tunnel emitting light for a brief period of time, etc.
     * 
     * @param index the index in the array to place the light object at
     * @param light the light object to add
     */
    public static void addLight(int index, Light light) {
        try {
            scene.lights[index] = light;
        } catch(IndexOutOfBoundsException e) {
            Logger.logWarning("Failed to add light at index " + index, e);
        }
    }
    
}