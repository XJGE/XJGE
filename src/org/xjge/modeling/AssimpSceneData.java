package org.xjge.modeling;

import org.xjge.graphics.Material;
import java.util.List;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
final class AssimpSceneData {

    final List<Mesh2> meshes;
    final List<Material2> materials;
    final List<Integer> meshMaterialIndices;
    
    AssimpSceneData(List<Mesh2> meshes, List<Material2> materials, List<Integer> meshMaterialIndices) {
        this.meshes              = meshes;
        this.materials           = materials;
        this.meshMaterialIndices = meshMaterialIndices;
    }
    
}