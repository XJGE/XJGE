package org.xjge.graphics;

import org.xjge.core.ErrorUtils;
import org.xjge.core.XJGE;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

//Created: Jun 15, 2021

/**
 * Objects of this class can be used as part of a larger 
 * {@link org.xjge.core.Widget UI widget} to represent a 
 * regular 2D shape such as a pentagon, hexagon, or circle by utilizing the 
 * number of sides specified through its constructor.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Polygon {

    private final int numSides;
    public boolean fill;
    public Color color;
    private final Graphics g = new Graphics();
    
    /**
     * Creates a new n-sided polygon object which can be used to represent 
     * regular shapes and circles. 
     * 
     * @param numSides the number of sides this shape will exhibit, (5 for a 
     *                 pentagon, 6 for a hexagon, etc.)
     * @param fill     if true, the shape will be filled with the specified 
     *                 color
     * @param radius   the radius used to determine the size of the polygon
     * @param color    the color to render this shape in
     * @param xPos     the x-coordinate of the shapes initial position in the 
     *                 viewport
     * @param yPos     the y-coordinate of the shapes initial position in the 
     *                 viewport
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
            g.vertices = stack.mallocFloat(this.numSides * 3);
            for(int v = 0; v < this.numSides; v++) g.vertices.put(vertX[v]).put(vertY[v]).put(-100);
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        translate(xPos, yPos);
    }
    
    /**
     * Alternate version of the 
     * {@link Polygon(int, boolean, float, Color, int, int) Polygon()} 
     * constructor.
     * 
     * @param numSides the number of sides this shape will exhibit, (5 for a 
     *                 pentagon, 6 for a hexagon, etc.)
     * @param fill     if true, the shape will be filled with the specified 
     *                 color
     * @param radius   the radius used to determine the size of the polygon
     * @param color    the color to render this shape in
     * @param position the position at which the shape will be placed initially
     */
    public Polygon(int numSides, boolean fill, float radius, Color color, Vector2i position) {
        this(numSides, fill, radius, color, position.x, position.y);
    }
    
    /**
     * Translates the polygon to the position specified. 
     * <p>
     * NOTE: polygons are positioned around their centerpoints.
     * 
     * @param position the position where the shape will be placed
     */
    public final void translate(Vector2i position) {
        g.modelMatrix.translation(position.x, position.y, 0);
    }
    
    /**
     * Alternate version of {@link #translate(Vector2i)}.
     * 
     * @param x the x coordinate to place this shape in the window
     * @param y the y coordinate to place this shape in the window
     */
    public final void translate(int x, int y) {
        g.modelMatrix.translation(x, y, 0);
    }
    
    /**
     * Rotates the polygon according to the angle specified.
     * 
     * @param angle the value indicating the rotation of the shape
     */
    public final void rotate(float angle) {
        g.modelMatrix.rotateZ((float) Math.toRadians(angle * -1f));
    }
    
    /**
     * Draws the polygon using the data specified by the constructor.
     */
    public void render() {
        XJGE.getDefaultGLProgram().use();
        
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 2);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uColor", color.asVec3());
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, numSides);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        g.freeBuffers();
    }
    
}