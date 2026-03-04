package org.xjge.modeling;

import org.xjge.graphics.Material;
import java.io.InputStream;
import java.util.List;
import org.xjge.core.Asset;
import org.xjge.core.AssetManager;
import org.xjge.core.Logger;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Model2 extends Asset {

    public List<Mesh2> meshes;
    public List<Material2> materials;
    public List<Integer> meshMaterialIndices;
    
    //TODO: Skeleton/Animations, etc.
    
    public static Model2 load(String filename) {
        return AssetManager.load(filename, () -> new Model2(filename));
    }
    
    private Model2(String filename) {
        super(filename);
    }

    @Override
    protected void onLoad(InputStream file) {
        try {
            var assimpSceneData = AssimpLoader.parse(file);
            meshes              = assimpSceneData.meshes;
            materials           = assimpSceneData.materials;
            meshMaterialIndices = assimpSceneData.meshMaterialIndices;
        } catch(Exception exception) {
            Logger.logWarning("Failed to load model: \"" + getFilename() + "\"", exception);
        }
    }

    @Override
    protected void onRelease() {
        meshes.forEach(Mesh2::release);
        meshes = null;
        materials = null;
        meshMaterialIndices = null;
    }

    @Override
    protected <T extends Asset> T onLoadFailure() {
        return null; //TODO: fallback model
    }
    
    //TODO: add getters
    
}