package org.xjge.graphics;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class BoneMask {

    private final Set<Integer> bones = new HashSet<>();
    
    public void add(int boneIndex) {
        bones.add(boneIndex);
    }
    
    public void remove(int boneIndex) {
        bones.remove(boneIndex);
    }
    
    public boolean contains(int boneIndex) {
        return bones.contains(boneIndex);
    }
    
}