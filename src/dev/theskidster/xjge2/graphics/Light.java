package dev.theskidster.xjge2.graphics;

import org.joml.Vector3f;

//Created: Jun 12, 2021

/**
 * Data structure that contains information which can be used to simulate 
 * visible light.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Light {

    public boolean enabled = true;
    
    public float brightness;
    public float contrast;
    
    public Vector3f position;
    public Color ambientColor;
    public Color diffuseColor;
    
    /**
     * Creates a new light object that contains data which can be used by the 
     * fragment shader during lighting calculations.
     * 
     * @param brightness   the intensity and range of the light. Should be a 
     *                     non-negative value between 0 and 1.
     * @param contrast     the noticeable difference between the intensity of 
     *                     the ambient and diffuse colors of this light. Should 
     *                     be a non-negative value between 0 and 1.
     * @param position     the position from which the light will be emitted.
     * @param ambientColor the ambient color that will be used to color the 
     *                     shaded side of an entity
     * @param diffuseColor the color that will be noticeably reflected off 
     *                     nearby entities
     */
    public Light(float brightness, float contrast, Vector3f position, Color ambientColor, Color diffuseColor) {
        this.brightness   = brightness;
        this.contrast     = contrast;
        this.position     = position;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * mimics daylight.
     * 
     * @return a new engine-provided light object
     */
    public static final Light daylight() {
        return new Light(0.55f, 0.15f, new Vector3f(-2, 4, 1), Color.WHITE, Color.WHITE);
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * simulates the hue of a sunset.
     * 
     * @return a new engine-provided light object
     */
    public static final Light sunset() {
        return new Light(0.7f, 0.46f, new Vector3f(-1, 1, -2), Color.create(173, 141, 162), Color.create(255, 204, 76));
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * resembles moonlight.
     * 
     * @return a new engine-provided light object
     */
    public static final Light midnight() {
        return new Light(0.95f, 1, new Vector3f(1, 2, 2), Color.create(35, 45, 74), Color.WHITE);
    }
    
}