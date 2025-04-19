package org.xjge.graphics;

import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * Created: May 26, 2021
 * <br><br>
 * Used to batch render rectangles. A batch should be done inside of a widgets 
 * render method like so:
 * <blockquote><pre>
 * batchStart(1.0f);
 *     drawRectangle()
 *     drawRectangle()
 *     drawRectangle()
 *     ...
 * batchEnd();
 * </pre></blockquote>
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class RectangleBatch {
    
    private int numVertices;
    
    private float opacity;
    
    private final Graphics g = new Graphics();
    
    /**
     * Establishes a system through which vertex data may be streamed to draw rectangles. 
     * 
     * @param numRectangles the maximum number of rectangles this batch is allowed to draw
     */
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
    
    /**
     * Renders every rectangle provided to the batch.
     */
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
    /**
     * Begins the batch rendering process.
     * 
     * @param opacity the transparency value of each rectangle in the batch
     */
    public void batchStart(float opacity) {
        this.opacity = opacity;
        numVertices  = 0;
    }
    
    /**
     * Finalizes the data and sends it to the GPU to be rendered.
     */
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
    
    /**
     * Draws a rectangle using the data provided. The shape will be drawn 
     * starting from the position of its bottom-left corner.
     * 
     * @param x the x-coordinate of the rectangles bottom-left corner
     * @param y the y-coordinate of the rectangles bottom-left corner
     * @param width  the width (in pixels) of the rectangle
     * @param height the height (in pixels) of the rectangle
     * @param color the color to draw the rectangle in
     */
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        int startIndex = (numVertices / 24) * Float.BYTES;
        
        g.vertices.put(x)        .put(y + height).put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y + height).put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x + width).put(y)         .put(0).put(color.r).put(color.g).put(color.b);
        g.vertices.put(x)        .put(y)         .put(0).put(color.r).put(color.g).put(color.b);
        
        g.indices.put(startIndex)    .put(startIndex + 1).put(startIndex + 2);
        g.indices.put(startIndex + 3).put(startIndex + 2).put(startIndex);
        
        numVertices += 24;
    }
    
    /**
     * Draws a rectangle using the data provided. The shape will be drawn 
     * starting from the position of its bottom-left corner.
     * 
     * @param pos the position in the viewport to draw the rectangle from
     * @param width the width (in pixels) of the rectangle
     * @param height the height (in pixels) of the rectangle
     * @param color the color to draw the rectangle in
     */
    public void drawRectangle(Vector2i pos, int width, int height, Color color) {
        drawRectangle(pos.x, pos.y, width, height, color);
    }
    
    /**
     * Draws a rectangle using the data provided.
     * 
     * @param rectangle the rectangle to draw
     * @param color the color to draw the rectangle in
     */
    public void drawRectangle(Rectangle rectangle, Color color) {
        drawRectangle(
                rectangle.xPos,
                rectangle.yPos,
                rectangle.width,
                rectangle.height,
                color);
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
        g.freeBuffers();
    }
    
}