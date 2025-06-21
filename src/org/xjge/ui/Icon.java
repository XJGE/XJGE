package org.xjge.ui;

import org.xjge.core.ErrorUtils;
import org.xjge.core.Logger;
import org.xjge.core.XJGE;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.UI;
import org.xjge.graphics.Atlas;
import org.xjge.graphics.Color;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * Used to represent a symbol on the user interface.
 * 
 * @author J Hoffman
 * @since 2.0.0
 * 
 * @see Widget
 */
public final class Icon {

    private float opacity = 1f;
    
    private int cellX;
    private int cellY;
    
    /**
     * Stores the position of the icon.
     */
    public final Vector2f position = new Vector2f();
    
    /**
     * Stores the size of the icon along the X and Y axes. By default the icon 
     * will have a scale of 1 for both dimensions. 
     */
    public final Vector2f scale = new Vector2f(1);
    
    /**
     * Stores the rotation angle of the icon along the XYZ axes.
     * <br><br>
     * Icons will rotate in a clockwise fashion. That is, if you supply this 
     * method with an value of 90 degrees the icon will rotate right, a value
     * of -90 will rotate it left, and so on. The maximum accepted rotation 
     * value in any direction is 360.
     */
    public final Vector3f rotation = new Vector3f();
    
    private final Vector2i atlasKey = new Vector2i();
    private Vector2f texCoords      = new Vector2f();
    
    private Color color = Color.WHITE;
    
    private final Texture texture;
    private final Atlas atlas;
    private final Graphics graphics;
    
    /**
     * Creates a new icon object using the data provided.
     * 
     * @param texture the texture image (or atlas) to use
     * @param cellWidth the width (in pixels) of each sub-image cell
     * @param cellHeight the height (in pixels) of each sub-image cell
     * @param fromCenter if true the icon will be positioned from its center 
     *                   instead of its bottom-left corner
     */
    public Icon(Texture texture, int cellWidth, int cellHeight, boolean fromCenter) {
        this.texture = texture;
        
        texture.bind(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        atlas    = new Atlas(texture, cellWidth, cellHeight);
        graphics = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            graphics.vertices = stack.mallocFloat(20);
            graphics.indices  = stack.mallocInt(6);
            
            if(fromCenter) {
                //(vec3 position), (vec2 tex coords)
                graphics.vertices.put(-atlas.cellWidth / 2).put( atlas.cellHeight / 2).put(0)  .put(0)                  .put(0);
                graphics.vertices.put( atlas.cellWidth / 2).put( atlas.cellHeight / 2).put(0)  .put(atlas.subImageWidth).put(0);
                graphics.vertices.put( atlas.cellWidth / 2).put(-atlas.cellHeight / 2).put(0)  .put(atlas.subImageWidth).put(atlas.subImageHeight);
                graphics.vertices.put(-atlas.cellWidth / 2).put(-atlas.cellHeight / 2).put(0)  .put(0)                  .put(atlas.subImageHeight);
            } else {
                //(vec3 position), (vec2 tex coords)
                graphics.vertices.put(0)              .put(atlas.cellHeight).put(0)    .put(0)                  .put(0);
                graphics.vertices.put(atlas.cellWidth).put(atlas.cellHeight).put(0)    .put(atlas.subImageWidth).put(0);
                graphics.vertices.put(atlas.cellWidth).put(0)               .put(0)    .put(atlas.subImageWidth).put(atlas.subImageHeight);
                graphics.vertices.put(0)              .put(0)               .put(0)    .put(0)                  .put(atlas.subImageHeight);
            }
            
            graphics.indices.put(0).put(1).put(2);
            graphics.indices.put(2).put(3).put(0);
            
            graphics.vertices.flip();
            graphics.indices.flip();
        }
        
        graphics.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    /**
     * Changes the opacity (or transparency) of the icon.
     * 
     * @param opacity a number (between 0 and 1) indicating how transparent the icon is
     */
    public void setOpacity(float opacity) {
        this.opacity = XJGE.clampValue(0, 1, opacity);
    }
    
    /**
     * Sets the sub-image this icon will use.
     * 
     * @param x the horizontal cell location as it appears in the texture atlas
     * @param y the vertical cell location as it appears in the texture atlas
     */
    public void setSubImage(int x, int y) {
        atlasKey.set(x, y);
        
        if(atlas.subImageOffsets.containsKey(atlasKey)) {
            cellX     = x;
            cellY     = y;
            texCoords = atlas.subImageOffsets.get(atlasKey);
        } else {
            Logger.logWarning("Failed to set icon sprite. The cell: (" + x +
                              ", " + y + ") is out of bounds",
                              null);
        }
    }
    
    /**
     * Changes the color of this icon.
     * 
     * @param color the color that the icon will be rendered in
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Obtains the current opacity (or transparency) value of this icon.
     * 
     * @return a number (between 0 and 1) indicating how transparent the icon is
     */
    public float getOpacity() {
        return opacity;
    }
    
    /**
     * Obtains the horizontal index of the sub-image cell.
     * 
     * @return a number indicating the cell index along the x-axis (zero indexed)
     */
    public int getSubImageX() {
        return cellX;
    }
    
    /**
     * Obtains the vertical index of the sub-image cell.
     * 
     * @return a number indicating the cell index along the y-axis (zero indexed)
     */
    public int getSubImageY() {
        return cellY;
    }
    
    /**
     * Obtains the current color of the icon.
     * 
     * @return an object representing a 3-component RGB color
     */
    public final Color getColor() {
        return color;
    }
    
    /**
     * Renders the icon image.
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
        glActiveTexture(GL_TEXTURE0);
        texture.bind(GL_TEXTURE_2D);
        glBindVertexArray(graphics.vao);
        
        UIShader.getInstance().setUniform("uType", 3);
        UIShader.getInstance().setUniform("uOpacity", opacity);
        UIShader.getInstance().setUniform("uColor", color.asVec3());
        UIShader.getInstance().setUniform("uModel", graphics.modelMatrix);
        UIShader.getInstance().setUniform("uProjection", UI.getProjectionMatrix());
        UIShader.getInstance().setUniform("uTexCoords", texCoords);
        UIShader.getInstance().setUniform("uTexture", 0);
        
        glDrawElements(GL_TRIANGLES, graphics.indices.limit(), GL_UNSIGNED_INT, 0);
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