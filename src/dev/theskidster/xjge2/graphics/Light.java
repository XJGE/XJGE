package dev.theskidster.xjge2.graphics;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Jun 12, 2021
 */

public final class Light {

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