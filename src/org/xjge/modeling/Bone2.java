package org.xjge.modeling;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Bone2 {

    public int parentIndex = -1;
    
    public String name;
    
    public List<Integer> children = new ArrayList<>();
    
    public final Matrix4f bindTransform        = new Matrix4f();
    public final Matrix4f inverseBindTransform = new Matrix4f();
    
}