package org.xjge.graphics;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.xjge.core.EntityComponent;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class ModelAnimator extends EntityComponent {
    
    private final Model model;
    
    private final BonePose[] tempPoseA;
    private final BonePose[] tempPoseB;
    private final BonePose[] layerPose;
    private final BonePose[] finalPose;
    
    private final Matrix4f[] localTransforms  = new Matrix4f[Mesh.MAX_BONES]; //Local transform for each bone relative to their parent
    private final Matrix4f[] jointTransforms  = new Matrix4f[Mesh.MAX_BONES]; //Resolved heirarchy matrices for joint attachment
    private final Matrix4f[] skinningMatrices = new Matrix4f[Mesh.MAX_BONES]; //Final skinning matrices used by the renderer

    public ModelAnimator(Model model) {
        this.model = model;
        
        int boneCount = model.getSkeleton().getBoneCount();
        
        tempPoseA = new BonePose[boneCount];
        tempPoseB = new BonePose[boneCount];
        layerPose = new BonePose[boneCount];
        finalPose = new BonePose[boneCount];
        
        for(int i = 0; i < boneCount; i++) {
            tempPoseA[i] = new BonePose();
            tempPoseB[i] = new BonePose();
            layerPose[i] = new BonePose();
            finalPose[i] = new BonePose();
        }
        
        for(int i = 0; i < Mesh.MAX_BONES; i++) {
            localTransforms[i]  = new Matrix4f().identity();
            jointTransforms[i]  = new Matrix4f().identity();
            skinningMatrices[i] = new Matrix4f().identity();
        }
    }

    public void update(double deltaTime, List<SkeletalAnimationLayer> layers) {
        if(layers == null || layers.isEmpty()) return;

        var firstLayer = true;
        
        for(var layer : layers) {
            layer.update(deltaTime);
            
            if(layer.isBlending()) {
                calculatePose(layer.getCurrent(), tempPoseA);
                calculatePose(layer.getNext(), tempPoseB);
                blendPoses(layer.getBlendFactor(), layerPose);
            } else {
                calculatePose(layer.getCurrent(), layerPose);
            }
            
            if(firstLayer) {
                for(int i = 0; i < model.getSkeleton().getBoneCount(); i++) finalPose[i].set(layerPose[i]);
                firstLayer = false;
            } else {
                blendLayer(layer);
            }
        }
        
        buildMatrices(finalPose);
    }

    private void calculatePose(SkeletalAnimation instance, BonePose[] output) {
        if(instance == null) return;
        
        var skeleton      = model.getSkeleton();
        var animationTime = instance.getAnimationTime();
        
        for(int i = 0; i < skeleton.getBoneCount(); i++) {
            Bone bone       = skeleton.getBone(i);
            BoneTrack track = instance.getBoneTrack(i);
            BonePose pose   = output[i];
            
            if(track != null) {
                pose.position.set(sampleVector3(track.positionTimes, track.positions, animationTime));
                pose.rotation.set(sampleQuaternion(track.rotationTimes, track.rotations, animationTime));
                pose.scale.set(sampleVector3(track.scaleTimes, track.scales, animationTime));
            } else {
                bone.localBindTransform.getTranslation(pose.position);
                bone.localBindTransform.getUnnormalizedRotation(pose.rotation);
                bone.localBindTransform.getScale(pose.scale);
            }
        }
    }
    
    private void blendPoses(float factor, BonePose[] output) {
        for(int i = 0; i < model.getSkeleton().getBoneCount(); i++) {
            output[i].set(tempPoseA[i]).interpolate(tempPoseB[i], factor);
        }
    }
    
    private void blendLayer(SkeletalAnimationLayer layer) {
        for(int i = 0; i < model.getSkeleton().getBoneCount(); i++) {
            if(layer.getMask() != null && !layer.getMask().contains(i)) continue;
            finalPose[i].interpolate(layerPose[i], layer.getWeight());
        }
    }
    
    private void buildMatrices(BonePose[] pose) {
        Skeleton skeleton = model.getSkeleton();
        
        for(int i = 0; i < skeleton.getBoneCount(); i++) {
            Bone bone = skeleton.getBone(i);
            
            localTransforms[i].identity().translate(pose[i].position).rotate(pose[i].rotation).scale(pose[i].scale);

            if(bone.parentIndex >= 0) {
                jointTransforms[i].set(jointTransforms[bone.parentIndex]).mul(localTransforms[i]);
            } else {
                jointTransforms[i].set(localTransforms[i]);
            }

            skinningMatrices[i].set(jointTransforms[i]).mul(bone.offsetMatrix);
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
    
    /**
     * Returns animated model-space bone transforms BEFORE the inverse bind correction is applied.
     * @return a matrix array containing the current model-space transforms of every bone in the skeleton
     */
    public Matrix4fc[] getJointTransforms() {
        return jointTransforms;
    }
    
    public Matrix4fc[] getSkinningMatrices() {
        return skinningMatrices;
    }
    
}