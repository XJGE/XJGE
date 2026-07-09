package org.xjge.graphics;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.xjge.core.Logger;

/**
 * Stores the local and world space transforms of a bone so other objects (swords, potions, items, etc.) may be anchored to it
 * 
 * @author J Hoffman
 * @since  4.0.0
 */
public final class JointAttachment {

    private int boneIndex = -1;
    
    private String boneName;
    
    private final Matrix4f localOffset    = new Matrix4f().identity();
    private final Matrix4f worldTransform = new Matrix4f();
    
    public void attach(Model model, String boneName) {
        if(model.getSkeleton().hasBone(boneName)) {
            this.boneName = boneName;
            boneIndex     = model.getSkeleton().getBoneIndex(boneName);
        } else {
            Logger.logWarning(
                "Invalid bone \"" + boneName + "\" used during joint attachment to model \"" + model.getFilename() +"\"", 
                null);
        }
    }
    
    /**
     * NOTE: must be called after ModelAnimator.update()
     * 
     * @param transform
     * @param animator 
     */
    public void update(Transform transform, ModelAnimator animator) {
        worldTransform.identity()
            .mul(transform.getModelMatrix())
            .mul(animator.getBoneTransforms()[boneIndex])
            .mul(localOffset);
    }
    
    public Matrix4fc getLocalOffset() {
        return localOffset;
    }
    
    public Matrix4fc getWorldTransform() {
        return worldTransform;
    }
    
}