package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Color;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 24, 2021
 */

public final class Background {

    private final int FLOATS_PER_RECTANGLE = 18;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    public Background() {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, FLOATS_PER_RECTANGLE * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    public void drawRectangle(float xPos, float yPos, float width, float height, Color color, float opacity) {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType",    3);
        XJGE.getDefaultGLProgram().setUniform("uOpacity", opacity);
        XJGE.getDefaultGLProgram().setUniform("uColor",   color.asVec3());
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertexBuf = stack.mallocFloat(FLOATS_PER_RECTANGLE);
            
            //(vec3 position)
            vertexBuf.put(xPos)        .put(yPos)         .put(0);
            vertexBuf.put(xPos + width).put(yPos)         .put(0);
            vertexBuf.put(xPos + width).put(yPos + height).put(0);
            vertexBuf.put(xPos + width).put(yPos + height).put(0);
            vertexBuf.put(xPos)        .put(yPos + height).put(0);
            vertexBuf.put(xPos)        .put(yPos)         .put(0);
            
            vertexBuf.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuf);
        }
        
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    public void drawRectangle(Rectangle rectangle, Color color, float opacity) {
        drawRectangle(rectangle.xPos, rectangle.yPos, rectangle.width, rectangle.height, color, opacity);
    }
    
    public void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
    
}