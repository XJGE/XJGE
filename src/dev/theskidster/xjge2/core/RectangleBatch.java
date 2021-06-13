package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Color;
import dev.theskidster.xjge2.graphics.Graphics;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: May 26, 2021
 */

public final class RectangleBatch {
    
    private int numVertices;
    
    private float opacity;
    
    private final Graphics g = new Graphics();
    
    public RectangleBatch(int numRectangles) {
        g.vertices = MemoryUtil.memAllocFloat(24 * numRectangles);
        g.indices  = MemoryUtil.memAllocInt(6 * numRectangles);
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, g.indices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    private void render() {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, g.indices);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 3);
        XJGE.getDefaultGLProgram().setUniform("uOpacity", opacity);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    public void batchStart(float opacity) {
        this.opacity = opacity;
        numVertices  = 0;
    }
    
    public void batchEnd() {
        if(numVertices > 0) {
            g.vertices.flip();
            g.indices.flip();
            
            render();
            
            g.vertices.clear();
            g.indices.clear();
            
            numVertices = 0;
        }
    }
    
    public void drawRectangle(float x, float y, float width, float height, Color color) {
        int startIndex = (numVertices / 24) * Float.BYTES;
        
        g.vertices.put(x)        .put(y + height).put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y + height).put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y)         .put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x)        .put(y)         .put(0).put(color.r).put(color.g).put(color.b);
        
        g.indices.put(startIndex)    .put(startIndex + 1).put(startIndex + 2);
        g.indices.put(startIndex + 3).put(startIndex + 2).put(startIndex);
        
        numVertices += 24;
    }
    
    public void drawRectangle(Vector3f pos, float width, float height, Color color) {
        drawRectangle(pos.x, pos.y, width, height, color);
    }
    
    public void drawRectangle(Rectangle rectangle, Color color) {
        drawRectangle(
                rectangle.xPos,
                rectangle.yPos,
                rectangle.width,
                rectangle.height,
                color);
    }
    
    public void freeBuffers() {
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
        g.freeBuffers();
    }
    
}