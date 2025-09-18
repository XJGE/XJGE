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

    final Vector3f attacker = new Vector3f();
    final Vector3f defender = new Vector3f();
    final float yaw, pitch;
    final float duration; //In seconds
    
    ShotMelee(Vector3f attacker, Vector3f defender, float yaw, float pitch, float duration) {
        this.attacker.set(attacker);
        this.defender.set(defender);
        this.yaw = yaw;
        this.pitch = pitch;
        this.duration = duration;
    }
    
}