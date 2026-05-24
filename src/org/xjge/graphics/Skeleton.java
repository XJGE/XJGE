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

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Skeleton {

    Matrix4f[] inverseBindPose;
    
    List<Bone> bones = new ArrayList<>();
    
    Map<String, Integer> boneMap = new HashMap<>();
    
    void buildBoneHierarchy(AINode node, int parentIndex) {
        var nodeName  = node.mName().dataString();
        var boneIndex = boneMap.get(nodeName);

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
    
    public int getBoneCount() {
        return bones.size();
    }
    
    public int getBoneIndex(String name) {
        return boneMap.get(name);
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