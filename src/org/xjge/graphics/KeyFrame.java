package org.xjge.graphics;

import java.util.ArrayList;
import org.joml.Matrix4f;

//Created: Jun 17, 2021

/**
 * Data structure which represents a single frame of a 
 * {@link SkeletalAnimation}. More specifically, a keyframe contains the 
 * individual transformations of each {@link Bone} in the models armature. Key 
 * frames can be conceptualized as a sort of "snapshot" of the armatures pose 
 * at some point in time which can be used in sequence with other keyframes to 
 * create the illusion of movement.
 */
class KeyFrame {

    final ArrayList<Matrix4f> transforms = new ArrayList<>();
    
    /**
     * Constructs an array of {@link Bone} transformations that will be used to 
     * define the pose of a models armature at a certain point in time.
     */
    KeyFrame() {
        for(int b = 0; b < Model.MAX_BONES; b++) transforms.add(new Matrix4f());
    }
    
}