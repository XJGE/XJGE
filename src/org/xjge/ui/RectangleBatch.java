package org.xjge.ui;

import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.ErrorUtils;
import org.xjge.core.UI;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;

/**
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
 * @since 2.0.0
 */
public final class RectangleBatch {
    
    private int numVertices;
    
    private final Graphics graphics = new Graphics();
    
    /**
     * Establishes a system through which vertex data may be streamed to draw rectangles. 
     * 
     * @param numRectangles the maximum number of rectangles this batch is allowed to draw
     */
    public RectangleBatch(int numRectangles) {
        graphics.vertices = MemoryUtil.memAllocFloat(28 * numRectangles);
        graphics.indices  = MemoryUtil.memAllocInt(6 * numRectangles);
        
        glBindVertexArray(graphics.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, graphics.vbo);
        glBufferData(GL_ARRAY_BUFFER, graphics.vertices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, graphics.ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, graphics.indices.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (7 * Float.BYTES), 0);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, (7 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(4);
    }
    
    /**
     * Renders every rectangle provided to the batch.
     */
    private void render() {
        UIShader.getInstance().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(graphics.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, graphics.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, graphics.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, graphics.ibo);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, graphics.indices);
        
        UIShader.getInstance().setUniform("uType", 1);
        UIShader.getInstance().setUniform("uProjection", UI.getProjectionMatrix());
        
        glDrawElements(GL_TRIANGLES, graphics.indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    /**
     * Begins the batch rendering process.
     */
    public void batchStart() {
        numVertices  = 0;
    }
    
    /**
     * Finalizes the data and sends it to the GPU to be rendered.
     */
    public void batchEnd() {
        if(numVertices > 0) {
            graphics.vertices.flip();
            graphics.indices.flip();
            
            render();
            
            graphics.vertices.clear();
            graphics.indices.clear();
            
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
    public void drawRectangle(int x, int y, int width, int height, Color color, float opacity) {
        int startIndex = (numVertices / 24) * Float.BYTES;
        
        graphics.vertices.put(x)        .put(y + height).put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
        graphics.vertices.put(x + width).put(y + height).put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
        graphics.vertices.put(x + width).put(y)         .put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
        graphics.vertices.put(x)        .put(y)         .put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
        
        graphics.indices.put(startIndex)    .put(startIndex + 1).put(startIndex + 2);
        graphics.indices.put(startIndex + 3).put(startIndex + 2).put(startIndex);
        
        numVertices += 28;
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
    public void drawRectangle(Vector2i pos, int width, int height, Color color, float opacity) {
        drawRectangle(pos.x, pos.y, width, height, color, opacity);
    }
    
    /**
     * Draws a rectangle using the data provided.
     * 
     * @param rectangle the rectangle to draw
     * @param color the color to draw the rectangle in
     */
    public void drawRectangle(Rectangle rectangle, Color color, float opacity) {
        drawRectangle(
                rectangle.positionX,
                rectangle.positionY,
                rectangle.width,
                rectangle.height,
                color,
                opacity);
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        MemoryUtil.memFree(graphics.vertices);
        MemoryUtil.memFree(graphics.indices);
        graphics.freeBuffers();
    }
    
}