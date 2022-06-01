package org.xjge.graphics;

import java.nio.FloatBuffer;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;

/**
 *
 * @author thesk
 */
public class Point {

    private int vao;
    private int vbo;
    
    private FloatBuffer vertices;
    
    public final Vector3f position;
    
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
