package org.xjge.ui;

import org.xjge.core.ErrorUtils;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.UI;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;

/**
 * Represents a convex 2D shape with any number of sides such as a pentagon, 
 * hexagon, heptagon and so on.
 * 
 * @author J Hoffman
 * @since 2.0.0
 * 
 * @see Widget
 */
public class Polygon {

    public boolean fill;
    
    private final int numberSides;
    
    public Color color;
    
    private final Graphics graphics = new Graphics();
    
    /**
     * Creates a new n-sided polygon object which can be used to represent 
     * regular shapes and circles. 
     * 
     * @param numberSides the number of sides this polygon will have
     * @param radius the radius (in pixels) used to determine the polygons size
     * @param positionX the initial horizontal position of the polygon
     * @param positionY the initial vertical position of the polygon
     * @param color the color this polygon will be rendered in
     * @param fill if true, the shape will be filled with the specified color
     */
    public Polygon(int numberSides, float radius, int positionX, int positionY, Color color, boolean fill) {
        this.numberSides = numberSides;
        this.fill        = fill;
        this.color       = color;
        
        float doublePI     = (float) (Math.PI * 2f);
        float[] vertexPosX = new float[this.numberSides];
        float[] vertexPosY = new float[this.numberSides];
        
        for(int v = 0; v < this.numberSides; v++) {
            vertexPosX[v] = (float) (radius * Math.cos(v * doublePI / this.numberSides));
            vertexPosY[v] = (float) (radius * Math.sin(v * doublePI / this.numberSides));
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(this.numberSides * 3);
            for(int v = 0; v < this.numberSides; v++) graphics.vertices.put(vertexPosX[v]).put(vertexPosY[v]).put(0);
            graphics.vertices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        translate(positionX, positionY);
    }
    
    /**
     * Alternate version of {@link #translate(Vector2i)}.
     * 
     * @param x the x coordinate to place this shape in the window
     * @param y the y coordinate to place this shape in the window
     */
    public final void translate(int x, int y) {
        graphics.modelMatrix.translation(x, y, 0);
    }
    
    /**
     * Rotates the polygon according to the angle specified.
     * 
     * @param angle the value indicating the rotation of the shape
     */
    public final void rotate(float angle) {
        graphics.modelMatrix.rotateZ((float) Math.toRadians(angle * -1f));
    }
    
    /**
     * Draws the polygon using the data specified by the constructor.
     */
    public void render(float opacity) {
        UIShader.getInstance().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(graphics.vao);
        
        UIShader.getInstance().setUniform("uType", 2);
        UIShader.getInstance().setUniform("uModel", graphics.modelMatrix);
        UIShader.getInstance().setUniform("uProjection", UI.getProjectionMatrix());
        UIShader.getInstance().setUniform("uColor", color.asVec3());
        UIShader.getInstance().setUniform("uOpacity", opacity);
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, numberSides);
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        graphics.freeBuffers();
    }
    
}