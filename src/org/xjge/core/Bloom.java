package org.xjge.core;

import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;

//Created: Dec 8, 2021

/**
 * @author J Hoffman
 * @since  2.0.0
 */
final class Bloom {
    
    final int texHandle;
    private final Graphics g;

    Bloom(int width, int height) {
        /*
        TODO:
        
        Not sure if this should follow the same structure as the shadow map or
        not. it requires a framebuffer but uses ping pong on top of the default
        one- might be better off to keep all the framebuffer objects in one place
        i dunno this is gonna need to be organized better when its all said and done.
        */
        
        texHandle = glGenTextures();
        
        glBindTexture(GL_TEXTURE_2D, texHandle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(0)    .put(height).put(0)    .put(1).put(1);
            g.vertices.put(width).put(height).put(0)    .put(0).put(1);
            g.vertices.put(width).put(0)     .put(0)    .put(0).put(0);
            g.vertices.put(0)    .put(0)     .put(0)    .put(1).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(3).put(2).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    void generate(GLProgram blurProgram, int handle, boolean horizontal) {
        
    }
    
}