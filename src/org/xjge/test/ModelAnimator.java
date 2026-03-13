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
        for (int i = 0; i < boneCount; i++) {
            runtime.currentPose[i] = new Matrix4f();
            runtime.skinningMatrices[i] = new Matrix4f();
        }
    }

    private void sampleAnimation(float t) {
        Skeleton skel = model.skeleton;

        for (int i = 0; i < skel.bones.size(); i++) {
            Bone2 bone = skel.bones.get(i);
            Keyframe2 keys = currentAnimation.keyframes.get(bone.name);

            Matrix4f localAnim = new Matrix4f();

            if (keys == null) {
                // No animation for this bone, identity local
                localAnim.identity();
            } else {
                Vector3f pos = samplePosition(keys, t);
                Quaternionf rot = sampleRotation(keys, t);
                Vector3f scale = sampleScale(keys, t);
                localAnim.translationRotateScale(pos, rot, scale);
            }

            runtime.currentPose[i].set(localAnim);
        }

        // Build global pose and skinning matrices
        for (int i = 0; i < skel.bones.size(); i++) {
            int parent = skel.bones.get(i).parentIndex;
            if (parent >= 0)
                runtime.currentPose[i].set(runtime.currentPose[parent]).mul(runtime.currentPose[i]);

            runtime.skinningMatrices[i].set(runtime.currentPose[i]).mul(skel.bones.get(i).inverseBindTransform);
        }
    }
    
    private int findKey(float t, float[] times) {
        for (int i = 0; i < times.length - 1; i++)
            if (t < times[i + 1])
                return i;

        return times.length - 1;
    }
    
    private Vector3f samplePosition(Keyframe2 k, float t) {
        if (k.positions.length == 1)
            return k.positions[0];

        int i = findKey(t, k.positionTimes);
        int j = Math.min(i + 1, k.positions.length - 1);

        float t0 = k.positionTimes[i];
        float t1 = k.positionTimes[j];

        float f = (t - t0) / (t1 - t0);

        return new Vector3f(k.positions[i]).lerp(k.positions[j], f);
    }
    
    private Quaternionf sampleRotation(Keyframe2 k, float t) {
        if (k.rotations.length == 1)
            return k.rotations[0];

        int i = findKey(t, k.rotationTimes);
        int j = Math.min(i + 1, k.rotations.length - 1);

        float t0 = k.rotationTimes[i];
        float t1 = k.rotationTimes[j];

        float f = (t - t0) / (t1 - t0);

        return new Quaternionf(k.rotations[i]).slerp(k.rotations[j], f);
    }
    
    private Vector3f sampleScale(Keyframe2 k, float t) {
        if (k.scales.length == 1)
            return k.scales[0];

        int i = findKey(t, k.scaleTimes);
        int j = Math.min(i + 1, k.scales.length - 1);

        float t0 = k.scaleTimes[i];
        float t1 = k.scaleTimes[j];

        float f = (t - t0) / (t1 - t0);

        return new Vector3f(k.scales[i]).lerp(k.scales[j], f);
    }

    public void update(double dt) {
        if (currentAnimation == null) return;
        time += dt * currentAnimation.ticksPerSecond;
        sampleAnimation(time % currentAnimation.duration);
    }

    public void setCurrentAnimation(String name) {
        currentAnimation = model.animations.stream()
                .filter(a -> a.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public Matrix4f[] getSkinningMatrices() { return runtime.skinningMatrices; }
    
}