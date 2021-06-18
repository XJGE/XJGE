package dev.theskidster.xjge2.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: May 7, 2021
 */

public final class Graphics {

    public final int vao = glGenVertexArrays();
    public final int vbo = glGenBuffers();
    public final int ibo = glGenBuffers();
    
    public FloatBuffer vertices;
    public IntBuffer indices;
    
    public Matrix4f modelMatrix = new Matrix4f();
    
    public void bindBuffers() {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        if(indices != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }
    }
    
    static Matrix4f convertFromAssimp(AIMatrix4x4 aiMatrix) {
        Matrix4f conversion = new Matrix4f();
        
        conversion.m00(aiMatrix.a1());
        conversion.m10(aiMatrix.a2());
        conversion.m20(aiMatrix.a3());
        conversion.m30(aiMatrix.a4());
        
        conversion.m01(aiMatrix.b1());
        conversion.m11(aiMatrix.b2());
        conversion.m21(aiMatrix.b3());
        conversion.m31(aiMatrix.b4());
        
        conversion.m02(aiMatrix.c1());
        conversion.m12(aiMatrix.c2());
        conversion.m22(aiMatrix.c3());
        conversion.m32(aiMatrix.c4());
        
        conversion.m03(aiMatrix.d1());
        conversion.m13(aiMatrix.d2());
        conversion.m23(aiMatrix.d3());
        conversion.m33(aiMatrix.d4());
        
        return conversion;
    }
    
    public void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
}