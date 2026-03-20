package org.xjge.graphics;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.assimp.AINodeAnim;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Keyframe {

    public int boneIndex;
    
    public float[] positionTimes;
    public float[] rotationTimes;
    public float[] scaleTimes;
    
    public Vector3f[] positions;
    public Vector3f[] scales;
    public Quaternionf[] rotations;
    
    Keyframe(AINodeAnim aiChannel, int boneIndex) {
        this.boneIndex = boneIndex;
        extractPositionFrames(aiChannel);
        extractRotationFrames(aiChannel);
        extractScaleFrames(aiChannel);
    }
    
    private void extractPositionFrames(AINodeAnim aiChannel) {
        int count     = aiChannel.mNumPositionKeys();
        var aiKeys    = aiChannel.mPositionKeys();
        positionTimes = new float[count];
        positions     = new Vector3f[count];

        for(int i = 0; i < count; i++) {
            var aiKey        = aiKeys.get(i);
            var aiValue      = aiKey.mValue();
            positionTimes[i] = (float) aiKey.mTime();
            positions[i]     = new Vector3f(aiValue.x(), aiValue.y(), aiValue.z());
        }
    }
    
    private void extractRotationFrames(AINodeAnim aiChannel) {
        int count     = aiChannel.mNumRotationKeys();
        var aiKeys    = aiChannel.mRotationKeys();
        rotationTimes = new float[count];
        rotations     = new Quaternionf[count];

        for(int i = 0; i < count; i++) {
            var aiKey        = aiKeys.get(i);
            var aiValue      = aiKey.mValue();
            rotationTimes[i] = (float) aiKey.mTime();
            rotations[i]     = new Quaternionf(aiValue.x(), aiValue.y(), aiValue.z(), aiValue.w());
        }
    }
    
    private void extractScaleFrames(AINodeAnim aiChannel) {
        int count  = aiChannel.mNumScalingKeys();
        var aiKeys = aiChannel.mScalingKeys();
        scaleTimes = new float[count];
        scales     = new Vector3f[count];

        for(int i = 0; i < count; i++) {
            var aiKey     = aiKeys.get(i);
            var aiValue   = aiKey.mValue();
            scaleTimes[i] = (float) aiKey.mTime();
            scales[i]     = new Vector3f(aiValue.x(), aiValue.y(), aiValue.z());
        }
    }
    
}