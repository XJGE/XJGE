package org.xjge.modeling;

import java.util.List;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class AssimpSceneData {

    final Skeleton skeleton;
    
    final List<Mesh2> meshes;
    final List<Material2> materials;
    final List<Integer> meshMaterialIndices;
    final List<SkeletalAnimation2> animations;
    
    AssimpSceneData(Skeleton skeleton, List<Mesh2> meshes, List<Material2> materials, List<Integer> meshMaterialIndices, 
                    List<SkeletalAnimation2> animations) {
        this.skeleton            = skeleton;
        this.meshes              = meshes;
        this.materials           = materials;
        this.meshMaterialIndices = meshMaterialIndices;
        this.animations          = animations;
    }
    
}