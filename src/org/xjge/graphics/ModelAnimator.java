package org.xjge.graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelAnimator extends EntityComponent {
    
    private double blendTime     = 0f;
    private double blendDuration = 0f;
    
    private AnimationInstance current;
    private AnimationInstance next;
    
    private final Model model;
    
    private final Matrix4f[] finalBoneMatrices = new Matrix4f[Mesh.MAX_BONES];

    public ModelAnimator(Model model) {
        this.model = model;
        for(int i = 0; i < Mesh.MAX_BONES; i++) finalBoneMatrices[i] = new Matrix4f().identity();
    }
    
    public void play(String animationName) {
        var animation = model.getAnimation(animationName);
        if(animation == null) return;

        current   = new AnimationInstance(animation);
        next      = null;
        blendTime = 0f;
    }
    
    public void crossfade(String animationName, float duration) {
        var animation = model.getAnimation(animationName);
        if(animation == null) return;

        if(current == null) {
            play(animationName);
            return;
        }

        next          = new AnimationInstance(animation);
        blendDuration = Math.max(duration, 0.0001f);
        blendTime     = 0f;
    }
    
    public void stop() {
        current = null;
        next    = null;
    }
    
    public void setLooping(boolean looping) {
        if(current != null) current.looping = looping;
    }
    
    public void setTime(float time) {
        if(current != null) current.time = time;
    }
    
    public void setNormalizedTime(float time) {
        if(current != null) {
            double durationSeconds = current.animation.duration / current.animation.ticksPerSecond;
            current.time = time * durationSeconds;
        }
    }
    
    public void setSpeed(float speed) {
        if(current != null) current.speed = speed;
    }
    
    public boolean isPlaying() {
        return current != null;
    }
    
    public boolean isFinished() {
        if(current == null) return true;
        if(next != null) return false;
        return current.isFinished();
    }
    
    public boolean justFinished() { //Only fired once upon initial finish, similar to buttonPressedOnce()
        if(current == null) return false;
        if(next != null) return false;
        return current.justFinished();
    }
    
    public String getCurrentAnimation() {
        return current != null ? current.animation.name : null;
    }
    
    public Matrix4f[] getFinalBoneMatrices() {
        return finalBoneMatrices;
    }

    public void update(double deltaTime) {
        if(current == null) return;

        current.update(deltaTime);

        if(next != null) {
            next.update(deltaTime);

            blendTime += deltaTime;
            float alpha = (float) Math.min(blendTime / blendDuration, 1.0);

            calculateBlendedPose(alpha);

            if(alpha >= 1f) {
                current = next;
                next = null;
            }
        } else {
            calculatePose(current, finalBoneMatrices);
        }
    }
    
    private void calculateBlendedPose(float alpha) {
        int boneCount = model.getSkeleton().bones.size();

        Matrix4f[] poseA = new Matrix4f[boneCount];
        Matrix4f[] poseB = new Matrix4f[boneCount];

        for(int i = 0; i < boneCount; i++) {
            poseA[i] = new Matrix4f();
            poseB[i] = new Matrix4f();
        }

        calculatePose(current, poseA);
        calculatePose(next, poseB);

        for(int i = 0; i < boneCount; i++) {
            finalBoneMatrices[i].set(poseA[i]).lerp(poseB[i], alpha);
        }
    }

    private void calculatePose(AnimationInstance instance, Matrix4f[] output) {
        var skeleton = model.getSkeleton();
        int boneCount = skeleton.bones.size();

        Matrix4f[] globalTransforms = new Matrix4f[boneCount];
        for(int i = 0; i < boneCount; i++) {
            globalTransforms[i] = new Matrix4f();
        }

        float animationTime = instance.getAnimationTime();

        for(int i = 0; i < boneCount; i++) {
            Bone bone         = skeleton.bones.get(i);
            Keyframe keyframe = instance.getKeyframe(i);
            Matrix4f localTransform;

            if(keyframe != null) {
                Vector3f pos   = sampleVector3(keyframe.positionTimes, keyframe.positions, animationTime);
                Quaternionf rot = sampleQuaternion(keyframe.rotationTimes, keyframe.rotations, animationTime);
                Vector3f scale = sampleVector3(keyframe.scaleTimes, keyframe.scales, animationTime);

                localTransform = new Matrix4f()
                        .translate(pos)
                        .rotate(rot)
                        .scale(scale);
            } else {
                localTransform = new Matrix4f(bone.localBindTransform);
            }

            if(bone.parentIndex >= 0) {
                globalTransforms[i].set(globalTransforms[bone.parentIndex]).mul(localTransform);
            } else {
                globalTransforms[i].set(localTransform);
            }

            output[i].set(globalTransforms[i]).mul(bone.offsetMatrix);
        }
    }
    
    private Vector3f sampleVector3(float[] times, Vector3f[] values, float t) {
        if(times.length == 0) return new Vector3f();
        if(times.length == 1) return new Vector3f(values[0]);

        int i = 0;
        while(i < times.length - 1 && t > times[i + 1]) i++;

        int nextTime = Math.min(i + 1, times.length - 1);

        float factor = (t - times[i]) / (times[nextTime] - times[i]);
        factor = Math.min(Math.max(factor, 0f), 1f);

        return new Vector3f(values[i]).lerp(values[nextTime], factor);
    }

    private Quaternionf sampleQuaternion(float[] times, Quaternionf[] values, float t) {
        if(times.length == 0) return new Quaternionf().identity();
        if(times.length == 1) return new Quaternionf(values[0]);

        int i = 0;
        while(i < times.length - 1 && t > times[i + 1]) i++;

        int nextTime = Math.min(i + 1, times.length - 1);

        float factor = (t - times[i]) / (times[nextTime] - times[i]);
        factor = Math.min(Math.max(factor, 0f), 1f);

        return new Quaternionf(values[i]).slerp(values[nextTime], factor);
    }
    
    private class AnimationInstance {
        
        boolean looping     = true;
        boolean wasFinished = false;
        
        double time  = 0.0;
        double speed = 1.0;
        
        SkeletalAnimation animation;
        
        Keyframe[] keyframesByBone;
        
        AnimationInstance(SkeletalAnimation animation) {
            this.animation  = animation;
            keyframesByBone = new Keyframe[model.getSkeleton().bones.size()];

            for(var keyframe : animation.keyframes) keyframesByBone[keyframe.boneIndex] = keyframe;
        }
        
        void update(double deltaTime) {
            time += deltaTime * speed;
            
            double durationSeconds = animation.duration / animation.ticksPerSecond;
            
            if(looping) {
                time %= durationSeconds;
            } else {
                if(time > durationSeconds) time = durationSeconds;
            }
        }
        
        boolean isFinished() {
            if(looping) return false;
            double durationSeconds = animation.duration / animation.ticksPerSecond;
            return time >= durationSeconds;
        }
        
        boolean justFinished() {
            boolean nowFinished = isFinished();
            boolean result      = nowFinished && !wasFinished;
            wasFinished         = nowFinished;
            return result;
        }
        
        float getAnimationTime() {
            double ticks = getTimeInTicks();
            
            if(looping) {
                ticks %= animation.duration;
            } else {
                if (ticks > animation.duration) {
                    ticks = animation.duration;
                }
            }

            return (float) ticks;
        }
        
        double getTimeInTicks() {
            return time * animation.ticksPerSecond;
        }

        Keyframe getKeyframe(int boneIndex) {
            return keyframesByBone[boneIndex];
        }
        
    }
    
}