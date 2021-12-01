package org.xjge.graphics;

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
    public float distance;
    
    public Vector3f position;
    public Color ambientColor;
    public Color diffuseColor;
    public Color specularColor;
    
    /**
     * Creates a new light object that contains data which can be used by the 
     * fragment shader during lighting calculations.
     * 
     * @param brightness the intensity and range of the light. Should be a 
     *                   non-negative value between 0 and 1.
     * @param contrast the noticeable difference between the intensity of 
     *                 the ambient and diffuse colors of this light. Should 
     *                 be a non-negative value between 0 and 1.
     * @param distance      
     * @param position      the position from which the light will be emitted.
     * @param ambientColor  the ambient color that will be used to color the 
     *                      shaded side of an entity
     * @param diffuseColor  the color that will be noticeably reflected off 
     *                      nearby entities
     * @param specularColor 
     */
    public Light(float brightness, float contrast, float distance, Vector3f position, Color ambientColor, Color diffuseColor, Color specularColor) {        
        this.brightness    = brightness;
        this.contrast      = clamp(0, 1, contrast);
        this.distance      = clamp(0, Float.MAX_VALUE, distance);
        this.position      = position;
        this.ambientColor  = ambientColor;
        this.diffuseColor  = diffuseColor;
        this.specularColor = specularColor;
    }
    
    //TODO: doc
    public Light(float brightness, float contrast, float distance, Vector3f position, Color color) {
        this(brightness, contrast, distance, position, color, color, color);
    }
    
    private float clamp(float minValue, float maxValue, float userValue) {
        float result = 0;
        
        if(userValue > maxValue) {
            result = maxValue;
        } else if(userValue < minValue) {
            result = minValue;
        } else {
            result = userValue;
        }
        
        return result;
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * mimics daylight.
     * 
     * @return a new engine-provided light object
     */
    public static final Light daylight() {
        return new Light(1, 0.55f, 1, new Vector3f(-10, 30, 7.5f), Color.WHITE);
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * simulates the hue of a sunset.
     * 
     * @return a new engine-provided light object
     */
    public static final Light sunset() {
        return new Light(0.87f, 0.5f, 1, new Vector3f(-8, 7.5f, -30), Color.create(173, 141, 162), Color.create(255, 204, 86), Color.create(255, 234, 184));
    }
    
    /**
     * Provides implementing applications with an predefined light object that 
     * resembles moonlight.
     * 
     * @return a new engine-provided light object
     */
    public static final Light midnight() {
        return new Light(0.82f, 0.55f, 1, new Vector3f(6.5f, 16.7f, 30), Color.create(38, 48, 76), Color.create(48, 62, 80), Color.create(188, 209, 231));
    }
    
    public static final Light beacon() {
        return new Light(3f, 1f, 1.25f, new Vector3f(2, 1, 0), Color.RED);
    }
    
}