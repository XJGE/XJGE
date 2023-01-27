package org.xjge.main;

//Created: Jan 25, 2023

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.PostProcessShader;


/**
 * @author J Hoffman
 * @since  
 */
public class PaletteProcess extends PostProcessShader {

    PaletteProcess(GLProgram glProgram) {
        super(glProgram);
        
        
    }
    
    @Override
    public void renderBefore(int viewTexHandle, int bloomTexHandle, Matrix4f projMatrix, Graphics g) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, viewTexHandle);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        /*
        TODO:
        
        Attempt to use post process effect to limit number of colors generated
        during fragment stage, the regular function used to blur and draw the
        framebuffer texture will still be called after.
        
        Consider letting these objects be called before and after the framebuffer
        texture draw.
        */
        
        glBindVertexArray(g.vao);
        
        glProgram.use();
        glProgram.setUniform("uTexture", 0);
        glProgram.setUniform("uProjection", false, projMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        ErrorUtils.checkGLError();
    }

    @Override
    public void renderAfter(int viewTexHandle, int bloomTexHandle, Matrix4f projMatrix, Graphics g) {
        //Unused
    }

}
