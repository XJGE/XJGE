package org.xjge.graphics;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Bone {
    
    int parentIndex;
    
    String name;
    
    Matrix4f offsetMatrix; //Inverse bind pose matrix
    Matrix4f localBindTransform;
    
    public int getParentIndex() {
        return parentIndex;
    }
    
    public String getName() {
        return name;
    }
    
    public Matrix4fc getOffsetMatrix() {
        return offsetMatrix;
    }
    
    public Matrix4fc getLocalBindTransform() {
        return localBindTransform;
    }
    
}