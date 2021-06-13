package dev.theskidster.xjge2.graphics;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jun 12, 2021
 */

public final class Light {

    
    public static final Light NOON     = new Light(0.55f, 0.15f, new Vector3f(-2, 4, 1), Color.WHITE, Color.WHITE);
    public static final Light SUNSET   = new Light(0.7f, 0.46f, new Vector3f(-1, 1, -2), Color.create(173, 141, 162), Color.create(255, 204, 76));
    public static final Light MIDNIGHT = new Light(0.95f, 1, new Vector3f(1, 2, 2), Color.create(35, 45, 74), Color.WHITE);
    
    public float brightness;
    public float contrast;
    
    public Vector3f position;
    public Color ambientColor;
    public Color diffuseColor;
    
    public Light(float brightness, float contrast, Vector3f position, Color ambientColor, Color diffuseColor) {
        this.brightness   = brightness;
        this.contrast     = contrast;
        this.position     = position;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
    }
    
}