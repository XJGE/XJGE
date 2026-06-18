package org.xjge.ui;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.core.UIManager;
import org.xjge.core.XJGE;
import org.xjge.graphics.Color;

/**
 * Data structure used by {@link RectangleBatch} objects to draw single color 
 * rectangles.
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public class Rectangle {

    private float opacity = 1f;
    
    private int vaoHandle = -1;
    private int vboHandle;
    private int iboHandle;
    
    public int width;
    public int height;
    
    private IntBuffer indices;
    
    public final Vector2i position = new Vector2i();
    public final Color color       = new Color(1f);
    
    /**
     * Creates a new rectangle object.
     */
    public Rectangle() {}
    
    public Rectangle(int width, int height) {
        this.width  = width;
        this.height = height;
    }
    
    /**
     * Creates a new rectangle object using the data specified. Rectangles are 
     * anchored around their bottom-left corner and use pixels for both 
     * coordinates and scale.
     * 
     * @param width the width of the rectangle (in pixels)
     * @param height the height of the rectangle (in pixels)
     * @param positionX the horizontal position of the rectangle
     * @param positionY the vertical position of the rectangle
     */
    public Rectangle(int width, int height, int positionX, int positionY) {
        this.width  = width;
        this.height = height;
        position.set(positionX, positionY);
    }
    
    public Rectangle(int width, int height, int positionX, int positionY, Color color, float opacity) {
        this.width   = width;
        this.height  = height;
        position.set(positionX, positionY);
        this.color.set(color);
        setOpacity(opacity);
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
     * A quick and dirty way to render a rectangle with minimal overhead.
     */
    public void render() {
        if(vaoHandle == -1) genBuffers();
        
        UIShader.getInstance().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(vaoHandle);
        
        UIShader.getInstance().setUniform("uType", 1);
        UIShader.getInstance().setUniform("uProjection", UIManager.getProjectionMatrix());
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer vertices = stack.mallocFloat(28);
            
            vertices.put(position.x)        .put(position.y + height).put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
            vertices.put(position.x + width).put(position.y + height).put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
            vertices.put(position.x + width).put(position.y)         .put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
            vertices.put(position.x)        .put(position.y)         .put(0).put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(opacity);
            
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
    
    /**
     * Determines if the specified point has intersected this rectangle.
     * 
     * @param pointX a value indicating the horizontal position of the point to check
     * @param pointY a value indicating the vertical position of the point to check
     * 
     * @return true if an intersection was detected
     */
    public boolean contains(int pointX, int pointY) {
        return (pointX > position.x && pointX < position.x + width) && 
               (pointY > position.y && pointY < position.y + height);
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
    
    public float getOpacity() {
        return opacity;
    }
    
    public final Rectangle setOpacity(float opacity) {
        this.opacity = XJGE.clampValue(0f, 1f, opacity);
        return this;
    }
    
}