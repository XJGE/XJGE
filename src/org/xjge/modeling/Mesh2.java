package org.xjge.modeling;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public class Mesh2 {

    public float[] positions;
    public float[] normals;
    public float[] texCoords;
    public float[] tangents;
    
    public int[] indices;
    
    Mesh2(float[] positions, float[] normals, float[] texCoords, float[] tangents, int[] indices) {
        this.positions = positions;
        this.normals = normals;
        this.texCoords = texCoords;
        this.tangents = tangents;
        this.indices = indices;
    }
    
    //TODO: bone weights
    
}