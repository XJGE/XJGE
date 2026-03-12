package org.xjge.modeling;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Keyframe2 {

    public float[] positionTimes;
    public float[] rotationTimes;
    public float[] scaleTimes;
    
    public Vector3f[] positions;
    public Vector3f[] scales;
    public Quaternionf[] rotations;
    
}