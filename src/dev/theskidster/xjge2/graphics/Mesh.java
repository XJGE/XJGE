package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 16, 2021
 */

final class Mesh {

    final int vao   = glGenVertexArrays();
    private int vbo = glGenBuffers();
    final int ibo   = glGenBuffers();
    
    int texIndex;
    
    IntBuffer indices;
    Matrix4f modelMatrix = new Matrix4f();
    
    Mesh(AIMesh aiMesh) {
        glBindVertexArray(vao);
        
        texIndex = aiMesh.mMaterialIndex(); //Determines which texture this mesh should use.
        
        parsePositionData(aiMesh);
        parseTexCoordData(aiMesh);
        parseNormalData(aiMesh);
        parseFaceData(aiMesh);
        
        glEnableVertexAttribArray(0); //position
        glEnableVertexAttribArray(2); //texture coordinates
        glEnableVertexAttribArray(3); //normal
    }
    
    private void parsePositionData(AIMesh aiMesh) {
        FloatBuffer positionBuf    = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 3);
        AIVector3D.Buffer aiVecBuf = aiMesh.mVertices();
        
        while(positionBuf.hasRemaining()) {
            AIVector3D aiVec = aiVecBuf.get();
            
            positionBuf.put(aiVec.x())
                       .put(aiVec.y())
                       .put(aiVec.z());
        }
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, positionBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(positionBuf);
        
        ErrorUtils.checkGLError();
    }
    
    private void parseTexCoordData(AIMesh aiMesh) {
        FloatBuffer texCoordBuf    = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 2);
        AIVector3D.Buffer aiVecBuf = aiMesh.mTextureCoords(0);
        
        if(aiVecBuf != null) {
            for(int i = 0; i < aiVecBuf.remaining(); i++) {
                AIVector3D aiVec = aiVecBuf.get(i);
                                
                texCoordBuf.put(aiVec.x())
                           .put(aiVec.y());
            }
        }
        
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(texCoordBuf);
        
        ErrorUtils.checkGLError();
    }
    
    private void parseNormalData(AIMesh aiMesh) {
        FloatBuffer normalBuf      = MemoryUtil.memAllocFloat(aiMesh.mNumVertices() * 3);
        AIVector3D.Buffer aiVecBuf = aiMesh.mNormals();
        
        if(aiVecBuf != null) {
            for(int i = 0; i < aiVecBuf.remaining(); i++) {
                AIVector3D aiVec = aiVecBuf.get(i);
                
                normalBuf.put(aiVec.x())
                         .put(aiVec.y())
                         .put(aiVec.z());
            }
        }
        
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, normalBuf.flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
        
        MemoryUtil.memFree(normalBuf);
        
        ErrorUtils.checkGLError();
    }
    
    private void parseFaceData(AIMesh aiMesh) {
        indices = MemoryUtil.memAllocInt(aiMesh.mNumFaces() * 3);
        AIFace.Buffer aiFaceBuf = aiMesh.mFaces();
        
        for(int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace aiFace = aiFaceBuf.get(i);
            indices.put(aiFace.mIndices());
        }
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.flip(), GL_STATIC_DRAW);
        
        ErrorUtils.checkGLError();
    }
    
    void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
}