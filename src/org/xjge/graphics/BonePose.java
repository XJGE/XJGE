package org.xjge.graphics;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Represents the runtime transform of a single bone in a models skeleton
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
final class BonePose {

    final Vector3f position    = new Vector3f();
    final Quaternionf rotation = new Quaternionf();
    final Vector3f scale       = new Vector3f(1f);
    
    BonePose() {}
    
    BonePose set(Vector3fc position, Quaternionfc rotation, Vector3fc scale) {
        this.position.set(position);
        this.rotation.set(rotation);
        this.scale.set(scale);
        
        return this;
    }
    
    BonePose set(BonePose other) {
        return set(other.position, other.rotation, other.scale);
    }
    
    BonePose interpolate(BonePose other, float t) {
        position.lerp(other.position, t);
        rotation.slerp(other.rotation, t);
        scale.lerp(other.scale, t);
        
        return this;
    }
    
}