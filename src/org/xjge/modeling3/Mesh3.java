package org.xjge.modeling3;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Mesh3 {
    
    public float[] positions;
    public float[] normals;
    public float[] tangents;
    public float[] uvs;
    public float[] boneWeights;
    
    public int vao;
    public int vbo;
    public int ebo;
    public int materialIndex;
    
    public int[] boneIDs;
    public int[] indices;
    
}