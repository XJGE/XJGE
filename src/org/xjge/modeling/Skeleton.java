package org.xjge.modeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Skeleton {

    public int rootBoneIndex;
    
    public List<Bone2> bones = new ArrayList<>();
    public Map<String, Integer> boneIndices = new HashMap<>();
    
}