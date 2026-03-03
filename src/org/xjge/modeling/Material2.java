package org.xjge.modeling;

import org.joml.Vector3f;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class Material2 {

    public float roughness     = 0.5f;
    public float metallic      = 0f;
    public float ambientFactor = 0.1f;
    
    public final Vector3f albedo = new Vector3f(1f);
    
    public Texture albedoMap;
    public Texture normalMap;
    public Texture metallicRoughnessMap;
    
}