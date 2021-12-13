package org.xjge.core;

import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.graphics.Graphics;

//Created: Dec 8, 2021

/**
 * @author J Hoffman
 * @since  2.0.0
 */
final class Bloom {
    
    private int width;
    private int height;
    
    final int[] fbos     = new int[2];
    final int[] textures = new int[3];
    
    private Graphics g = new Graphics();

    Bloom() {
        width  = Window.getWidth();
        height = Window.getHeight();
        
        glGenFramebuffers(fbos);
        glGenTextures(textures);
        
        createTextureAttachments();
    }
    
    void createTextureAttachments() {
        for(int i = 0; i < 3; i++) {
            glBindTexture(GL_TEXTURE_2D, textures[i]);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            glBindTexture(GL_TEXTURE_2D, 0);
            
            if(i != 2) {
                glBindFramebuffer(GL_FRAMEBUFFER, fbos[i]);
                    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textures[i], 0);
                    ErrorUtils.checkFBStatus(GL_FRAMEBUFFER);
                glBindFramebuffer(GL_FRAMEBUFFER, fbos[i]);
            }
        }
        
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
    
    void updateDimensions() {
        width  = Window.getWidth();
        height = Window.getHeight();
        
        createTextureAttachments();
    }
    
}