package org.xjge.graphics;

//Created: Jun 17, 2021

/**
 * Data structure that represents a vertex weight that will determine how much 
 * influence a {@link Bone} has on the vertices of a {@link Mesh}. Typically 
 * multiple weights will influence a single vertex to an extent.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
class VertexWeight {

    int boneID;
    int vertexID;
    float weight;
    
    /**
     * Creates a new vertex weight that couples the bone to the vertex it 
     * influences.
     * 
     * @param boneID   an index value used to identify the bone in the vertex 
     *                 shader
     * @param vertexID the id of the vertex this weight will effect
     * @param weight   the total influence of this weight between 0 and 1
     */
    VertexWeight(int boneID, int vertexID, float weight) {
        this.boneID   = boneID;
        this.vertexID = vertexID;
        this.weight   = weight;
    }
    
}