package org.xjge.graphics;

import org.joml.Matrix4f;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Bone {
    
    public int parentIndex;
    
    public String name;
    
    public Matrix4f offsetMatrix; //Inverse bind pose matrix
    public Matrix4f localBindTransform;
    
}