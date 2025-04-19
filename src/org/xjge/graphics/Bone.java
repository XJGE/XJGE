package org.xjge.graphics;

import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;

/**
 * Created: Jun 17, 2021
 * <br><br>
 * Data structure which represents a single bone of a greater skeleton (or 
 * "Armature") that will be used to calculate the offset of one or more meshes 
 * vertex positions in a {@link SkeletalAnimation}.
 */
class Bone {

    int id;
    String name;
    Matrix4f offset;
    
    /**
     * Defines fields which can be used to identify the bone in other parts of 
     * the engine along with an offset matrix that will be utilized to decouple 
     * the position of the bone relative to the local space of the 
     * {@link Model} with which it's associated.
     * 
     * @param id an index value used to identify the bone in the vertex shader
     * @param name the unique name of this bone that will correspond to a 
     *             {@link Node} object in the model files hierarchy
     * @param offset a matrix representing this bones position offset relative 
     *               to the model origin
     */
    Bone(int id, String name, AIMatrix4x4 offset) {
        this.id     = id;
        this.name   = name;
        this.offset = Graphics.convertFromAssimp(offset);
    }
    
}