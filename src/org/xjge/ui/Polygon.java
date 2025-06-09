package org.xjge.ui;

import org.xjge.core.ErrorUtils;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.UI;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;

/**
 * Created: Jun 15, 2021
 * <br><br>
 * Objects of this class can be used as part of a larger {@link org.xjge.core.Widget UI widget} 
 * to represent a regular 2D shape such as a pentagon, hexagon, or circle by
 * utilizing the number of sides specified through its constructor.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Polygon {

    private final int numSides;
    public boolean fill;
    public Color color;
    private final Graphics graphics = new Graphics();
    
    /**
     * Creates a new n-sided polygon object which can be used to represent 
     * regular shapes and circles. 
     * 
     * @param numSides the number of sides this shape will exhibit, (5 for a 
     *                 pentagon, 6 for a hexagon, etc.)
     * @param fill if true, the shape will be filled with the specified color
     * @param radius the radius used to determine the size of the polygon
     * @param color the color to render this shape in
     * @param xPos the x-coordinate of the shapes initial position in the viewport
     * @param yPos the y-coordinate of the shapes initial position in the viewport
     */
    public Polygon(int numSides, boolean fill, float radius, Color color, int xPos, int yPos) {
        this.numSides = numSides;
        this.fill     = fill;
        this.color    = color;
        
        float doublePI = (float) (Math.PI * 2f);
        float[] vertX  = new float[this.numSides];
        float[] vertY  = new float[this.numSides];
        
        for(int v = 0; v < this.numSides; v++) {
            vertX[v] = (float) (radius * Math.cos(v * doublePI / this.numSides));
            vertY[v] = (float) (radius * Math.sin(v * doublePI / this.numSides));
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(this.numSides * 3);
            for(int v = 0; v < this.numSides; v++) graphics.vertices.put(vertX[v]).put(vertY[v]).put(-100);
            graphics.vertices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        translate(xPos, yPos);
    }
    
    /**
     * Alternate version of the {@link Polygon(int, boolean, float, Color, int, int) Polygon()} 
     * constructor.
     * 
     * @param numSides the number of sides this shape will exhibit, (5 for a 
     *                 pentagon, 6 for a hexagon, etc.)
     * @param fill if true, the shape will be filled with the specified color
     * @param radius the radius used to determine the size of the polygon
     * @param color the color to render this shape in
     * @param position the position at which the shape will be placed initially
     */
    public Polygon(int numSides, boolean fill, float radius, Color color, Vector2i position) {
        this(numSides, fill, radius, color, position.x, position.y);
    }
    
    /**
     * Translates the polygon to the position specified. 
     * <p>
     * NOTE: Polygons are positioned around their centerpoints.
     * 
     * @param position the position where the shape will be placed
     */
    public final void translate(Vector2i position) {
        graphics.modelMatrix.translation(position.x, position.y, 0);
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
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, numSides);
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