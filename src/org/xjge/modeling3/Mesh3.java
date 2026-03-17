package org.xjge.modeling3;

import java.nio.IntBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.ErrorUtils;
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
    public int vbo1;
    public int vbo2;
    public int ibo;
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
        
        //Normalize bone weights (all four combined should not exceed 1.0f otherwise we'll have visual bugs)
        for(int v = 0; v < getVertexCount(); v++) {
            int base  = v * MAX_BONES_PER_VERTEX;
            float sum = 0f;

            for(int i = 0; i < MAX_BONES_PER_VERTEX; i++) sum += boneWeights[base + i];

            if(sum > 0f) {
                for(int i = 0; i < MAX_BONES_PER_VERTEX; i++) boneWeights[base + i] /= sum;
            }
        }
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
    
    void upload() {
        //Interleaved data: positions(3), normals(3), tangents(3), uvs(2), boneWeights(4)
        var vertexBuffer = MemoryUtil.memAllocFloat(getVertexCount() * 15);
        var boneIDBuffer = MemoryUtil.memAllocInt(getVertexCount() * 4);
        
        try {
            for(int i = 0; i < getVertexCount(); i++) {
                int io2 = i * 2; //Index offset for vec2
                int io3 = i * 3; //Index offset for vec3
                int io4 = i * 4; //Index offset for vec4

                vertexBuffer.put(positions[io3]).put(positions[io3 + 1]).put(positions[io3 + 2]);
                vertexBuffer.put(normals[io3]).put(normals[io3 + 1]).put(normals[io3 + 2]);
                vertexBuffer.put(tangents[io3]).put(tangents[io3 + 1]).put(tangents[io3 + 2]);
                vertexBuffer.put(uvs[io2]).put(uvs[io2 + 1]);
                vertexBuffer.put(boneWeights[io4]).put(boneWeights[io4 + 1]).put(boneWeights[io4 + 2]).put(boneWeights[io4 + 3]);

                boneIDBuffer.put(boneIDs[io4]).put(boneIDs[io4 + 1]).put(boneIDs[io4 + 2]).put(boneIDs[io4+ 3]);
            }

            vertexBuffer.flip();
            boneIDBuffer.flip();

            vao  = glGenVertexArrays();
            vbo1 = glGenBuffers();
            vbo2 = glGenBuffers();
            ibo  = glGenBuffers();

            int stride = 15 * Float.BYTES;

            glBindVertexArray(vao);

            glBindBuffer(GL_ARRAY_BUFFER, vbo1);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            //Positions
            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);

            //Normals
            glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            //Tangents
            glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, 6 * Float.BYTES);
            glEnableVertexAttribArray(2);

            //UVs
            glVertexAttribPointer(3, 2, GL_FLOAT, false, stride, 9 * Float.BYTES);
            glEnableVertexAttribArray(3);

            //Bone Weights
            glVertexAttribPointer(4, 4, GL_FLOAT, false, stride, 11 * Float.BYTES);
            glEnableVertexAttribArray(4);

            glBindBuffer(GL_ARRAY_BUFFER, vbo2);
            glBufferData(GL_ARRAY_BUFFER, boneIDBuffer, GL_STATIC_DRAW);

            //Bone IDs
            glVertexAttribIPointer(5, 4, GL_INT, 4 * Integer.BYTES, 0);
            glEnableVertexAttribArray(5);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            glBindVertexArray(0);
            ErrorUtils.checkGLError();
            
        } finally {
            MemoryUtil.memFree(vertexBuffer);
            MemoryUtil.memFree(boneIDBuffer);
        }
    }
    
    void delete() {
        //TODO: free OpenGL objects, call from model.onRelease();
    }
    
    int getVertexCount() {
        return positions.length / 3;
    }
    
}