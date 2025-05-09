package org.xjge.ui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Color;

/**
 * Created: May 25, 2021
 * <br><br>
 * Data structure used by {@link RectangleBatch} objects to draw single color 
 * rectangles.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Rectangle {

    private int vaoHandle = -1;
    private int vboHandle;
    private int iboHandle;
    
    public int positionX;
    public int positionY;
    public int width;
    public int height;
    
    private IntBuffer indices;
    
    /**
     * Creates a new rectangle object.
     */
    public Rectangle() {}
    
    /**
     * Creates a new rectangle object using the data specified. Rectangles are 
     * anchored around their bottom-left corner and use pixels for both 
     * coordinates and scale.
     * 
     * @param positionX the horizontal position of the rectangle
     * @param positionY the vertical position of the rectangle
     * @param width the width of the rectangle (in pixels)
     * @param height the height of the rectangle (in pixels)
     */
    public Rectangle(int positionX, int positionY, int width, int height) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width     = width;
        this.height    = height;
    }
    
    /**
     * Generates buffer objects for this rectangle to use during rendering. 
     * Called automatically by the engine once when {@link render render()} is 
     * first used.
     */
    private void genBuffers() {
        vaoHandle = glGenVertexArrays();
        vboHandle = glGenBuffers();
        iboHandle = glGenBuffers();
        
        glBindVertexArray(vaoHandle);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        glBufferData(GL_ARRAY_BUFFER, 28 * Float.BYTES, GL_DYNAMIC_DRAW);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            indices = stack.mallocInt(6);
            
            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);
            
            indices.flip();
        }
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (7 * Float.BYTES), 0);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, (7 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(4);
    }
    
    /**
     * Determines if the specified point has intersected this rectangle.
     * 
     * @param pointX a value indicating the horizontal position of the point to check
     * @param pointY a value indicating the vertical position of the point to check
     * 
     * @return true if an intersection was detected
     */
    public boolean contains(int pointX, int pointY) {
        return (pointX > positionX && pointX < positionX + width) && 
               (pointY > positionY && pointY < positionY + height);
    }
    
    /**
     * Vector2i variant of the {@link contains(int, int)} method.
     * 
     * @param point a 2D point inside the game window to check
     * 
     * @return true if an intersection was detected
     */
    public boolean contains(Vector2i point) {
        return contains(point.x, point.y);
    }
    
    /**
     * A quick and dirty way to render a rectangle with little overhead.
     * <p>
     * NOTE: Anytime a large number of rectangles needs to be drawn efficiently 
     * {@link RectangleBatch} should instead be used in place of this method.
     * 
     * @param opacity the transparency value of the rectangle
     * @param color the color to draw the rectangle in
     */
    public void render(float opacity, Color color) {
        if(vaoHandle == -1) genBuffers();
        
        UI.getInstance().shader.use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(vaoHandle);
        
        UI.getInstance().shader.setUniform("uType", 1);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(28);
            
            vertices.put(positionX)        .put(positionY + height).put(0).put(color.r).put(color.g).put(color.b).put(opacity);
            vertices.put(positionX + width).put(positionY + height).put(0).put(color.r).put(color.g).put(color.b).put(opacity);
            vertices.put(positionX + width).put(positionY)         .put(0).put(color.r).put(color.g).put(color.b).put(opacity);
            vertices.put(positionX)        .put(positionY)         .put(0).put(color.r).put(color.g).put(color.b).put(opacity);
            
            vertices.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        
        glDrawElements(GL_TRIANGLES, indices.capacity(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Convenience method which frees the default buffer objects defined by 
     * this class. Only required if {@link render render()} is called and this
     * object is no longer needed.
     */
    public void delete() {
        glDeleteVertexArrays(vaoHandle);
        glDeleteBuffers(vboHandle);
        glDeleteBuffers(iboHandle);
    }
    
}