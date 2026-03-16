package org.xjge.modeling3;

import java.nio.IntBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import static org.xjge.modeling3.Model3.MAX_BONES_PER_VERTEX;

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
    
    private enum VertexAttribute {
        
        POSITION(3),
        NORMAL(3),
        TANGENT(3),
        UV(2);
        
        final int stride;
        
        VertexAttribute(int stride) { this.stride = stride; }
        
    }
    
    Mesh3(AIMesh aiMesh, Skeleton3 skeleton) {
        positions = extractVertexAttribute(aiMesh, VertexAttribute.POSITION);
        normals   = extractVertexAttribute(aiMesh, VertexAttribute.NORMAL);
        tangents  = extractVertexAttribute(aiMesh, VertexAttribute.TANGENT);
        uvs       = extractVertexAttribute(aiMesh, VertexAttribute.UV);
        extractBoneWeights(aiMesh, skeleton);
        materialIndex = aiMesh.mMaterialIndex();
        extractIndices(aiMesh);
    }
    
    private float[] extractVertexAttribute(AIMesh aiMesh, VertexAttribute attribute) {
        AIVector3D.Buffer buffer;
        
        switch(attribute) {
            case POSITION -> buffer = aiMesh.mVertices();
            case NORMAL   -> buffer = aiMesh.mNormals();
            case TANGENT  -> buffer = aiMesh.mTangents();
            case UV       -> buffer = aiMesh.mTextureCoords(0);
            default       -> throw new IllegalArgumentException("Unsupported vertex attribute found: \"" + attribute + "\"");
        }
        
        if(buffer == null) return null;
        
        int vertexCount = aiMesh.mNumVertices();
        float[] result  = new float[vertexCount * attribute.stride];
        
        for(int i = 0; i < vertexCount; i++) {
            var vec = buffer.get(i);
            
            result[i * attribute.stride]     = vec.x();
            result[i * attribute.stride + 1] = vec.y();
            
            if(attribute.stride == 3) result[i * attribute.stride + 2] = vec.z();
        }
        
        return result;
    }
    
    private void extractIndices(AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        var faces     = aiMesh.mFaces();
        
        indices = new int[faceCount * 3];
        
        for(int i = 0; i < faceCount; i++) {
            AIFace aiFace      = faces.get(i);
            IntBuffer idx      = aiFace.mIndices();
            indices[i * 3]     = idx.get(0);
            indices[i * 3 + 1] = idx.get(1);
            indices[i * 3 + 2] = idx.get(2);
        }
    }
    
    private void extractBoneWeights(AIMesh aiMesh, Skeleton3 skeleton) {
        int vertexCount = aiMesh.mNumVertices();
        int boneCount   = aiMesh.mNumBones();
        var aiBones     = aiMesh.mBones();
        
        boneIDs     = new int[vertexCount * MAX_BONES_PER_VERTEX];
        boneWeights = new float[vertexCount * MAX_BONES_PER_VERTEX];
        
        for(int boneIndex = 0; boneIndex < boneCount; boneIndex++) {
            var aiBone   = AIBone.create(aiBones.get(boneIndex));
            var weights  = aiBone.mWeights();
            var boneName = aiBone.mName().dataString();
            
            //Blender likes to add pipes for some reason so we'll discard that if we see it
            int pipe = boneName.indexOf("|");
            if(pipe != -1) boneName = boneName.substring(pipe + 1);
            
            var mapIndex = skeleton.boneMap.get(boneName);
            
            //Build resolver list, this helps us convert local mesh space to global space during rendering
            if(mapIndex == null) {
                mapIndex          = skeleton.bones.size();
                var bone          = new Bone3();
                bone.name         = boneName;
                bone.offsetMatrix = Model3.convertMatrix(aiBone.mOffsetMatrix());
                skeleton.bones.add(bone);
                skeleton.boneMap.put(boneName, mapIndex);
            }
            
            for(int w = 0; w < aiBone.mNumWeights(); w++) {
                var vw       = weights.get(w);
                int vertexID = vw.mVertexId();
                float weight = vw.mWeight();
                int base     = vertexID * MAX_BONES_PER_VERTEX;
                
                for(int i = 0; i < MAX_BONES_PER_VERTEX; i++) {
                    if(boneWeights[base + i] == 0f) {
                        boneIDs[base + i]     = mapIndex;
                        boneWeights[base + i] = weight;
                        break;
                    }
                }
            }
        }
    }
    
}