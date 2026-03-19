package org.xjge.modeling3;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

        for(int i = 0; i < boneCount; i++) finalBoneMatrices[i] = new Matrix4f().identity();
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

        // All other bones identity
        //for(int i = 0; i < finalBoneMatrices.length; i++) {
            //finalBoneMatrices[i].identity();
        //}
        
        //finalBoneMatrices[1].identity().translate(0, 1, 0);
        
        calculateBoneTransforms(animationTime);
    }

    private void calculateBoneTransforms(float animationTime) {
        var skeleton = model.getSkeleton();
        int boneCount = skeleton.bones.size();
        
        // Prepare arrays
        Matrix4f[] globalTransforms = new Matrix4f[boneCount];
        for (int i = 0; i < boneCount; i++) globalTransforms[i] = new Matrix4f();

        // Step 1: build transforms for every bone
        for (int i = 0; i < boneCount; i++) {
            Bone3 bone = skeleton.bones.get(i);

            // Find keyframe for this bone
            Keyframe3 keyframe = findKeyframe(i);

            Matrix4f localTransform;
            if (keyframe != null) {
                Vector3f pos   = sampleVector3(keyframe.positionTimes, keyframe.positions, animationTime);
                Quaternionf rot = sampleQuaternion(keyframe.rotationTimes, keyframe.rotations, animationTime);
                Vector3f scale = sampleVector3(keyframe.scaleTimes, keyframe.scales, animationTime);

                localTransform = new Matrix4f().translate(pos).rotate(rot).scale(scale);
                
                /*
                System.out.println("bone name: " + bone.name + " animationTime: " + animationTime + 
                        " posTimesCount: " + keyframe.positionTimes.length +
                        " rotTimesCount: " + keyframe.positionTimes.length);
                */
            } else {
                // No keyframe: use identity (bind pose already in localBindTransform)
                localTransform = new Matrix4f(bone.localBindTransform);
                
                /*
                System.out.println("bone name: " + bone.name + "animationTime: " + animationTime + 
                        " no keyframe");
                */
            }

            // Step 2: accumulate along hierarchy
            if (bone.parentIndex >= 0) {
                globalTransforms[i].set(globalTransforms[bone.parentIndex]).mul(localTransform);
            } else {
                globalTransforms[i].set(localTransform);
            }

            // Step 3: apply offset matrix for skinning
            finalBoneMatrices[i].set(globalTransforms[i]).mul(bone.offsetMatrix);
        }
    }
    
    private Keyframe3 findKeyframe(int boneIndex) {
        for (Keyframe3 keyframe : currentAnimation.keyframes) {
            if (keyframe.boneIndex == boneIndex) {
                return keyframe;
            }
        }
        return null;
    }
    
    // Linear interpolation for Vector3f
    private Vector3f sampleVector3(float[] times, Vector3f[] values, float t) {
        if (times.length == 0) return new Vector3f();
        if (times.length == 1) return new Vector3f(values[0]);

        // find two surrounding keyframes
        int i = 0;
        while (i < times.length - 1 && t > times[i+1]) i++;

        int next = Math.min(i + 1, times.length - 1);
        float factor = (t - times[i]) / (times[next] - times[i]);
        factor = Math.min(Math.max(factor, 0f), 1f);

        return new Vector3f(values[i]).lerp(values[next], factor);
    }

    // Spherical linear interpolation for Quaternionf
    private Quaternionf sampleQuaternion(float[] times, Quaternionf[] values, float t) {
        if (times.length == 0) return new Quaternionf().identity();
        if (times.length == 1) return new Quaternionf(values[0]);

        int i = 0;
        while (i < times.length - 1 && t > times[i+1]) i++;

        int next = Math.min(i + 1, times.length - 1);
        float factor = (t - times[i]) / (times[next] - times[i]);
        factor = Math.min(Math.max(factor, 0f), 1f);

        return new Quaternionf(values[i]).slerp(values[next], factor);
    }
    
    public Matrix4f[] getFinalBoneMatrices() {
        return finalBoneMatrices;
    }
    
}