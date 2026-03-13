package org.xjge.modeling3;

import org.joml.Vector3f;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public class Material3 {
    
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