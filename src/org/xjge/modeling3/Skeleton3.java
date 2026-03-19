package org.xjge.modeling3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AINode;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Skeleton3 {

    public Matrix4f[] inverseBindPose;
    
    public List<Bone3> bones = new ArrayList<>();
    
    public Map<String, Integer> boneMap = new HashMap<>();
    
    void buildBoneHierarchy(AINode node, int parentIndex) {
        var nodeName  = node.mName().dataString();
        var boneIndex = boneMap.get(nodeName);

        if(boneIndex != null) {
            var bone = bones.get(boneIndex);
            
            bone.parentIndex = parentIndex;
            bone.localBindTransform = Model3.convertMatrix(node.mTransformation());
            
            parentIndex = boneIndex;
        }

        PointerBuffer children = node.mChildren();

        for(int i = 0; i < node.mNumChildren(); i++) {
            buildBoneHierarchy(AINode.create(children.get(i)), parentIndex);
        }
    }
    
}