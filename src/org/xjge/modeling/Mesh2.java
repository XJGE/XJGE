package org.xjge.modeling;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public class Mesh2 {

    public float[] positions;
    public float[] normals;
    public float[] uvs;
    public float[] tangents;
    public float[] boneWeights;
    
    public int[] boneIDs;
    public int[] indices;
    
    Mesh2(float[] positions, float[] normals, float[] uvs, float[] tangents, int[] indices) {
        this.positions = positions;
        this.normals   = normals;
        this.uvs       = uvs;
        this.tangents  = tangents;
        this.indices   = indices;
    }
    
    //TODO: provide read-only access to data, the asset manager should update this behind the scenes
    
}