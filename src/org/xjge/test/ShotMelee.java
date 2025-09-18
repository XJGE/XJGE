package org.xjge.test;

import org.joml.Vector3f;

/**
 * Represents a single camera shot, these are used in a larger sequence during
 * a melee RTR.
 * 
 * @author J Hoffman
 * @since 
 */
class ShotMelee {

    final Vector3f attackerPos = new Vector3f();
    final Vector3f defenderPos = new Vector3f();
    final float yaw, pitch;
    final float duration; //In seconds
    final float lerpFactor;
    
    ShotMelee(Vector3f attackerPos, Vector3f defenderPos, float yaw, float pitch, float duration, float lerpFactor) {
        this.attackerPos.set(attackerPos);
        this.defenderPos.set(defenderPos);
        this.yaw = yaw;
        this.pitch = pitch;
        this.duration = duration;
        this.lerpFactor = lerpFactor;
    }
    
}