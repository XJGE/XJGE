package dev.theskidster.xjge2.graphics;

/**
 * @author J Hoffman
 * Created: Jun 17, 2021
 */

class VertexWeight {

    int boneID;
    int vertexID;
    float weight;
    
    VertexWeight(int boneID, int vertexID, float weight) {
        this.boneID   = boneID;
        this.vertexID = vertexID;
        this.weight   = weight;
    }
    
}