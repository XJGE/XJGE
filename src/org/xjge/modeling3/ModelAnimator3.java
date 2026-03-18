package org.xjge.modeling3;

import org.joml.Matrix4f;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelAnimator3 extends EntityComponent {
    
    private float currentTime = 0f;
    
    private Model3 model;
    private SkeletalAnimation3 currentAnimation;

    private Matrix4f[] finalBoneMatrices;

    public ModelAnimator3(Model3 model) {
        this.model = model;

        int boneCount     = model.getSkeleton().bones.size();
        finalBoneMatrices = new Matrix4f[boneCount];

        for(int i = 0; i < boneCount; i++) finalBoneMatrices[i] = new Matrix4f();
    }

    public void play(SkeletalAnimation3 animation) {
        this.currentAnimation = animation;
        this.currentTime = 0f;
    }

    public void update(float deltaTime) {
        if(currentAnimation == null) return;

        currentTime += deltaTime;

        float duration = currentAnimation.duration;
        float ticksPerSecond = currentAnimation.ticksPerSecond != 0
                ? currentAnimation.ticksPerSecond
                : 25.0f;

        float timeInTicks   = currentTime * ticksPerSecond;
        float animationTime = timeInTicks % duration;

        calculateBoneTransforms(animationTime);
    }

    private void calculateBoneTransforms(float animationTime) {
        var skeleton = model.getSkeleton();
        float offset = 300.0f * (float)Math.sin(animationTime);
        
        for(int i = 0; i < skeleton.bones.size(); i++) {
            Matrix4f local = new Matrix4f().translate(0, offset, 0);
            finalBoneMatrices[i].set(local); // ignore offsets and parents
        }
    }
    
    private Matrix4f sampleAnimation(String boneName, float seekTime) {
        return null;
    }
    
    public Matrix4f[] getFinalBoneMatrices() {
        return finalBoneMatrices;
    }
    
}