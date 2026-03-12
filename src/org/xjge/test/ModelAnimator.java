package org.xjge.test;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xjge.core.EntityComponent;
import org.xjge.modeling.Bone2;
import org.xjge.modeling.Keyframe2;
import org.xjge.modeling.Model2;
import org.xjge.modeling.SkeletalAnimation2;
import org.xjge.modeling.Skeleton;
import org.xjge.modeling.SkeletonRuntime;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelAnimator extends EntityComponent {

    private float time;
    
    private final Model2 model;
    private final SkeletonRuntime runtime;
    private SkeletalAnimation2 currentAnimation;

    public ModelAnimator(Model2 model) {
        this.model = model;
        int boneCount = model.skeleton.bones.size();
        runtime = new SkeletonRuntime();
        runtime.currentPose = new Matrix4f[boneCount];
        runtime.skinningMatrices = new Matrix4f[boneCount];

        for(int i = 0; i < boneCount; i++) {
            runtime.currentPose[i] = new Matrix4f();
            runtime.skinningMatrices[i] = new Matrix4f();
        }
    }
    
    private void sampleAnimation(float time) {
        Skeleton skeleton = model.skeleton;

        for(int i = 0; i < skeleton.bones.size(); i++) {
            Bone2 bone = skeleton.bones.get(i);
            Keyframe2 keys = currentAnimation.keyframes.get(bone.name);

            if(keys == null) {
                runtime.currentPose[i].set(bone.bindTransform);
                continue;
            }

            Vector3f position = keys.positions[0];
            Quaternionf rotation = keys.rotations[0];
            Vector3f scale = keys.scales[0];

            runtime.currentPose[i]
                .identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
        }
    }
    
    private void buildGlobalPose() {
        Skeleton skeleton = model.skeleton;
        
        for(int i = 0; i < skeleton.bones.size(); i++) {
            int parent = skeleton.bones.get(i).parentIndex;
            if(parent >= 0) {
                // global = parentGlobal * local
                runtime.currentPose[i].set(runtime.currentPose[parent]).mul(runtime.currentPose[i]);
            }
        }
    }
    
    private void computeSkinningMatrices() {
        Skeleton skeleton = model.skeleton;

        for(int i = 0; i < skeleton.bones.size(); i++) {
            runtime.skinningMatrices[i]
                .set(runtime.currentPose[i])
                .mul(skeleton.bones.get(i).inverseBindTransform);
        }
    }
    
    public void update(double dt) {
        if(currentAnimation == null) return;

        time += dt * currentAnimation.ticksPerSecond;
        float animationTime = time % currentAnimation.duration;
        
        sampleAnimation(animationTime);
        buildGlobalPose();
        computeSkinningMatrices();
    }
    
    public void setCurrentAnimation(String name) {
        model.animations.forEach(animation -> {
            if(animation.name.equals(name)) currentAnimation = animation;
        });
    }
    
    public Matrix4f[] getSkinningMatrices() {
        return runtime.skinningMatrices;
    }
    
}