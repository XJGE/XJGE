package org.xjge.modeling3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Skeleton3 {

    public Matrix4f[] inverseBindPose;
    
    public List<Bone3> bones = new ArrayList<>();
    
    public Map<String, Integer> boneMap = new HashMap<>();
    
}