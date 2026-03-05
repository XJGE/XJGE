package org.xjge.modeling;

import org.joml.Vector3f;
import org.xjge.core.AssetManager;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Material2 {

    public float metallic;
    public float roughness;
    public float opacity = 1f;
    
    public TransparencyMode transparencyMode = TransparencyMode.OPAQUE;
    
    public final Vector3f albedoColor = new Vector3f(1f);
    
    public String albedoMapFilename;
    public String normalMapFilename;
    public String metallicMapFilename;
    public String roughnessMapFilename;
    
}