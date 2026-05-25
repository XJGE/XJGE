package org.xjge.graphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AINode;
import org.xjge.core.Logger;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Skeleton {

    Matrix4f[] inverseBindPose;
    
    private final List<Bone> bones = new ArrayList<>();
    
    private final Map<String, Integer> boneIndices = new HashMap<>();
    
    void buildBoneHierarchy(AINode node, int parentIndex) {
        var nodeName  = node.mName().dataString();
        var boneIndex = boneIndices.get(nodeName);

        if(boneIndex != null) {
            var bone = bones.get(boneIndex);
            
            bone.parentIndex = parentIndex;
            bone.localBindTransform = Model.convertMatrix(node.mTransformation());
            
            parentIndex = boneIndex;
        }

        PointerBuffer children = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++) {
            buildBoneHierarchy(AINode.create(children.get(i)), parentIndex);
        }
    }
    
    void unregisterBones() {
        bones.clear();
        boneIndices.clear();
    }
    
    int registerBone(Bone bone) {
        if(boneIndices.containsKey(bone.name)) {
            Logger.logWarning("Duplicate bone \"" + bone.name + "\" detected during import", new IllegalArgumentException());
        }
        
        int index = bones.size();
        bones.add(bone);
        boneIndices.put(bone.name, index);
        return index;
    }
    
    public boolean hasBone(String name) {
        return boneIndices.containsKey(name);
    }
    
    public int getBoneCount() {
        return bones.size();
    }
    
    public int getBoneIndex(String name) {
        return boneIndices.get(name);
    }
    
    public String getBoneName(int index) {
        return bones.get(index).name;
    }
    
    public Bone getBone(int index) {
        return bones.get(index);
    }
    
    public Bone getBone(String name) {
        return bones.get(getBoneIndex(name));
    }
    
    public List<Bone> getBones() {
        return Collections.unmodifiableList(bones);
    }
    
    public Matrix4fc[] getInverseBindPose() {
        return inverseBindPose.clone();
    }
    
}