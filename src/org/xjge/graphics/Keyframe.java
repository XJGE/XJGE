package org.xjge.graphics;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Keyframe {

    public final Vector3f position    = new Vector3f();
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f scale       = new Vector3f(1f);
    
    public Keyframe() {}
    
    public Keyframe set(Vector3fc position, Quaternionfc rotation, Vector3fc scale) {
        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);
        
        return this;
    }
    
    public Keyframe set(Keyframe other) {
        return set(other.position, other.rotation, other.scale);
    }
    
    public Keyframe interpolate(Keyframe other, float t) {
        position.lerp(other.position, t);
        rotation.slerp(other.rotation, t);
        scale.lerp(other.scale, t);
        
        return this;
    }
    
}