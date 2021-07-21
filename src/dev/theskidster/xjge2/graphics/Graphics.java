package dev.theskidster.xjge2.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIMatrix4x4;
import static org.lwjgl.opengl.GL30.*;

//Created: May 7, 2021

/**
 * Component object that supplies implementing objects with the following:
 * <ul>
 * <li>A vertex buffer object - As a default buffer through which the 
 *     implementing class may supply its vertex data to the 
 *     {@linkplain dev.theskidster.xjge2.graphics graphics pipeline}.</li>
 * <li>A index buffer object - To discourage data redundancy by specifying 
 *     which vertices to reuse for the objects model mesh.</li>
 * <li>A vertex array object - For providing convenient access to the default 
 *     vertex buffer in addition to any the implementation may define.</li>
 * </ul>
 * <p>
 * Implementing objects should define their vertex data and vertex attribute 
 * layouts in their constructors using LWJGLs memory utilities such as 
 * {@link org.lwjgl.system.MemoryUtil MemoryUtil} or 
 * {@link org.lwjgl.system.MemoryStack MemoryStack} (the later of which only if 
 * the vertex data doesn't exceed the JVMs stack size). LWJGLs 
 * {@link org.lwjgl.assimp.Assimp Assimp} binding is also available in 
 * conjunction to the previously mentioned classes to load vertex data in the 
 * form of a 3D model.
 * </p>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Graphics {

    public final int vao = glGenVertexArrays();
    public final int vbo = glGenBuffers();
    public final int ibo = glGenBuffers();
    
    public FloatBuffer vertices;
    public IntBuffer indices;
    
    public Matrix4f modelMatrix = new Matrix4f();
    
    /**
     * Convenience method provided to bind the default buffers defined by this 
     * class. Implementing classes are expected to define vertex attribute 
     * layouts following this call in their constructors with methods like 
     * {@link org.lwjgl.opengl.GL30#glVertexAttribPointer(int, int, int, boolean, int, java.nio.ByteBuffer) 
     * glVertexAttribPointer()}.
     */
    public void bindBuffers() {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        if(indices != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        }
    }
    
    /**
     * Convenience method which frees the default buffer objects defined by 
     * this class. Additional buffers required by the implementing object will 
     * need to be freed individually. This method should be called in 
     * conjunction with 
     * {@link org.lwjgl.system.MemoryUtil#memFree(java.nio.Buffer) 
     * MemoryUtil.memFree()} if additional data was used during the 
     * implementing objects lifetime.
     */
    public void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
    /**
     * Converts the matrix data structure provided by Assimp into a format the 
     * engine understands.
     * 
     * @param aiMatrix the Assimp matrix data we want to convert
     * @return         a new four-component engine-friendly matrix object
     */
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
    
}