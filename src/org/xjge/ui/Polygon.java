package org.xjge.ui;

import org.joml.Vector2f;
import org.xjge.core.ErrorUtils;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.UI;
import org.xjge.core.XJGE;
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

    /**
     * Determines if the shape should be filled with the rendering color or if 
     * only the outline should be visible.
     */
    public boolean fill = true;
    
    private float opacity = 1f;
    
    private final int sides;
    
    /**
     * Stores the position of the polygon. The origin point of these objects is
     * always set from the center.
     */
    public final Vector2f position = new Vector2f();
    
    /**
     * Stores the size of the polygon along the X and Y axes. By default the 
     * shape will have a scale of 1 for both dimensions. 
     */
    public final Vector2f scale = new Vector2f(1);
    
    /**
     * Stores the rotation angle of the polygon along the XYZ axes.
     * <br><br>
     * Polygons will rotate in a clockwise fashion. That is, if you supply this 
     * method with an value of 90 degrees the shape will rotate right, a value
     * of -90 will rotate it left, and so on. The maximum accepted rotation 
     * value in any direction is 360.
     */
    public final Vector3f rotation = new Vector3f();
    
    private Color color = Color.WHITE;
    
    private final Graphics graphics = new Graphics();
    
    /**
     * Creates a new n-sided polygon object which can be used to represent 
     * regular shapes and circles. 
     * 
     * @param sides the number of sides this polygon will have
     * @param radius the radius (in pixels) used to determine the polygons size
     */
    public Polygon(int sides, float radius) {
        this.sides = sides;
        
        float doublePI     = (float) (Math.PI * 2f);
        float[] vertexPosX = new float[this.sides];
        float[] vertexPosY = new float[this.sides];
        
        for(int v = 0; v < this.sides; v++) {
            vertexPosX[v] = (float) (radius * Math.cos(v * doublePI / this.sides));
            vertexPosY[v] = (float) (radius * Math.sin(v * doublePI / this.sides));
        }
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(this.sides * 3);
            for(int v = 0; v < this.sides; v++) graphics.vertices.put(vertexPosX[v]).put(vertexPosY[v]).put(0);
            graphics.vertices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
    }
    
    /**
     * Changes the opacity (or transparency) of the polygon.
     * 
     * @param opacity a number (between 0 and 1) indicating how transparent the shape is
     */
    public void setOpacity(float opacity) {
        this.opacity = XJGE.clampValue(0, 1, opacity);
    }
    
    /**
     * Changes the color of this polygon.
     * 
     * @param color the color that the shape will be rendered in
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtains the current opacity (or transparency) value of this polygon.
     * 
     * @return a number (between 0 and 1) indicating how transparent the shape is
     */
    public float getOpacity() {
        return opacity;
    }
    
    /**
     * Obtains the current color of the polygon.
     * 
     * @return an object representing a 3-component RGB color
     */
    public final Color getColor() {
        return color;
    }
    
    /**
     * Draws the polygon using the data specified by the constructor.
     */
    public void render() {
        graphics.modelMatrix.translation(position.x, position.y, 0);
        
        //TODO: check to make sure this doesnt promote object churn or gimbal lock
        float rotationX = (float) Math.toRadians(rotation.x * -1f);
        float rotationY = (float) Math.toRadians(rotation.y * -1f);
        float rotationZ = (float) Math.toRadians(rotation.z * -1f);
        
        graphics.modelMatrix.rotateXYZ(rotationX, rotationY, rotationZ);
        graphics.modelMatrix.scaleXY(scale.x, scale.y);
        
        UIShader.getInstance().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindVertexArray(graphics.vao);
        
        UIShader.getInstance().setUniform("uType", 2);
        UIShader.getInstance().setUniform("uModel", graphics.modelMatrix);
        UIShader.getInstance().setUniform("uProjection", UI.getProjectionMatrix());
        UIShader.getInstance().setUniform("uColor", color.asVector3f());
        UIShader.getInstance().setUniform("uOpacity", opacity);
        
        glDrawArrays((fill) ? GL_TRIANGLE_FAN : GL_LINE_LOOP, 0, sides);
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