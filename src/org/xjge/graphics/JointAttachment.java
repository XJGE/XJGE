package org.xjge.graphics;

import org.joml.Matrix4f;

/**
 * Stores the local and world space transforms of a bone so other objects (swords, potions, items, etc.) may be anchored to it
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class JointAttachment {

    public int boneIndex;
    
    public Matrix4f localOffset    = new Matrix4f().identity();
    public Matrix4f worldTransform = new Matrix4f();
    
    public void update(Transform transform, ModelAnimator animator) {
        worldTransform
                .identity()
                .mul(transform.getModelMatrix())
                .mul(animator.getBoneTransforms()[boneIndex])
                .mul(localOffset);
    }
    
}