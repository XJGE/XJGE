package org.xjge.core;

import org.joml.Vector3f;
import org.xjge.graphics.Color;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Light {

    boolean enabled;
    
    public float brightness = 1f;
    public float range      = 10f;
    public float falloff    = 1f;
    
    public LightType type = LightType.POINT;
    
    public final Vector3f position = new Vector3f();
    public final Color color       = new Color(1f, 1f, 1f);
    
    void reset() {
        enabled    = false;
        brightness = 1f;
        range      = 10f;
        falloff    = 1f;
        type       = LightType.POINT;
        position.zero();
        color.set(1f, 1f, 1f);
    }
    
}