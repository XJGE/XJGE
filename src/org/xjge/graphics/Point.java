package org.xjge.graphics;

import java.nio.FloatBuffer;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;

//Created Sep 6, 2024

/**
 * Represents a single point in 3D space. Largely used for debugging purposes.
 * 
 * @author J Hoffman
 * @since  3.0.3
 */
public class Point {

    private final int vao;
    private final int vbo;
    
    private final FloatBuffer vertices;
    
    public final Vector3f position;
    
    /**
     * Creates a new object representing a single point in 3D space.
     * 
     * @param x the location of the point along the x-axis
     * @param y the location of the point along the y-axis
     * @param z the location of the point along the z-axis
     */
    public Point(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertices = stack.mallocFloat(6);
            vertices.put(x).put(y).put(z).put(0).put(0).put(0);
            vertices.flip();
        }
        
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    /**
     * Renders the point using the default engine shaders.
     * 
     * @param size the size (in pixels) of the dot that will appear
     */
    public void render(int size) {
        glPointSize(size);
        
        glBindVertexArray(vao);
        
        XJGE.getDefaultGLProgram().use();
        XJGE.getDefaultGLProgram().setUniform("uType", 3);
        XJGE.getDefaultGLProgram().setUniform("uOpacity", 1.0f);
        
        glDrawArrays(GL_POINTS, 0, 1);
        
        glPointSize(1);
        
        ErrorUtils.checkGLError();
    }
    
}
