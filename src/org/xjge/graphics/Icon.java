package org.xjge.graphics;

import org.joml.Matrix4f;
import org.xjge.core.ErrorUtils;
import org.xjge.core.Logger;
import org.xjge.core.XJGE;
import org.joml.Vector2f;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

//Created: Jun 8, 2021

/**
 * Represents a quickly comprehensible symbol included to help users better 
 * understand an interface. Icons make use of a {@link Atlas} and as such, 
 * provide utilities for quickly switching between individual sub-images as 
 * needed.
 * 
 * @author J Hoffman
 * @since  2.0.0
 * 
 * @see org.xjge.core.Widget
 */
public final class Icon {

    private float opacity = 1.0f;
    private float scale   = 1.0f;
    private float angle;
    
    private Vector2f currCell = new Vector2f();
    private final Graphics g  = new Graphics();
    private final Texture texture;
    private final Atlas atlas;
    
    public Color color = Color.WHITE;
    
    /**
     * Creates a new icon object which can be used to comprise part of a larger 
     * user interface.
     * 
     * @param texture    the texture image to use
     * @param cellWidth  the width of each sub-image cell in pixels
     * @param cellHeight the height of each sub-image cell in pixels
     * @param fromCenter if true the icon will be positioned from its center 
     *                   instead of its bottom left corner
     */
    public Icon(Texture texture, int cellWidth, int cellHeight, boolean fromCenter) {
        this.texture = texture;
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        atlas = new Atlas(texture, cellWidth, cellHeight);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            if(fromCenter) {
                //(vec3 position), (vec2 tex coords)
                g.vertices.put(-atlas.cellWidth / 2).put( atlas.cellHeight / 2).put(0)  .put(0)                  .put(0);
                g.vertices.put( atlas.cellWidth / 2).put( atlas.cellHeight / 2).put(0)  .put(atlas.subImageWidth).put(0);
                g.vertices.put( atlas.cellWidth / 2).put(-atlas.cellHeight / 2).put(0)  .put(atlas.subImageWidth).put(atlas.subImageHeight);
                g.vertices.put(-atlas.cellWidth / 2).put(-atlas.cellHeight / 2).put(0)  .put(0)                  .put(atlas.subImageHeight);
            } else {
                //(vec3 position), (vec2 tex coords)
                g.vertices.put(0)              .put(atlas.cellHeight).put(0)    .put(0)                  .put(0);
                g.vertices.put(atlas.cellWidth).put(atlas.cellHeight).put(0)    .put(atlas.subImageWidth).put(0);
                g.vertices.put(atlas.cellWidth).put(0)               .put(0)    .put(atlas.subImageWidth).put(atlas.subImageHeight);
                g.vertices.put(0)              .put(0)               .put(0)    .put(0)                  .put(atlas.subImageHeight);
            }
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    /**
     * Creates a new icon object which can be used to comprise part of a larger 
     * user interface.
     * <p>
     * NOTE: A new version of this method exists as of version 2.1.17, which 
     * adds another parameter that can be used to position the icon by it's 
     * center.
     * 
     * @param texture    the texture image to use
     * @param cellWidth  the width of each sub-image cell in pixels
     * @param cellHeight the height of each sub-image cell in pixels
     */
    public Icon(Texture texture, int cellWidth, int cellHeight) {
        this(texture, cellWidth, cellHeight, false);
    }
    
    /**
     * Obtains the model matrix of the icon so it may be used to perform 
     * translation/rotate/scaling operations with greater fidelity.
     * 
     * @return the model matrix the icon uses during rendering
     */
    public Matrix4f getModelMatrix() {
        return g.modelMatrix;
    }
    
    /**
     * Sets the current position of the icon.
     * 
     * @param position the position to set this icon to in the viewport
     */
    public void setPosition(Vector2i position) {
        g.modelMatrix.translation(position.x, position.y, 0);
    }
    
    /**
     * Variant of {@link setPosition(Vector2i)}.
     * 
     * @param x the x-coordinate to place the icon at in the viewport
     * @param y the y-coordinate to place the icon at in the viewport
     */
    public void setPosition(int x, int y) {
        g.modelMatrix.translation(x, y, 0);
    }
    
    /**
     * Sets the sub-image this icon will use
     * 
     * @param cellX the location of the cell along the x-axis as it appears in 
     *              the texture atlas
     * @param cellY the location of the cell along the y-axis as it appears in 
     *              the texture atlas
     */
    public void setSubImage(int cellX, int cellY) {
        Vector2i key = new Vector2i(cellX, cellY);
        
        if(atlas.subImageOffsets.containsKey(key)) {
            currCell = atlas.subImageOffsets.get(key);
        } else {
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to set icon sprite. The cell: (" + cellX +
                              ", " + cellY + ") is out of bounds",
                              null);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Changes the transparency of the icon.
     * 
     * @param opacity a number between 0 and 1 used to indicate how transparent the icon will be
     */
    public void setOpacity(float opacity) {
        this.opacity = XJGE.clampValue(0, 1, opacity);
    }
    
    /**
     * Changes the size of the icon.
     * 
     * @param scale a non-negative number that will be used to scale the icon
     */
    public void setScale(float scale) {
        this.scale = Math.abs(scale);
        g.modelMatrix.scale(this.scale);
    }
    
    /**
     * Changes the rotation angle of the icon.
     * <p>
     * Icons will rotate in a clockwise fashion. That is, if you supply this 
     * method with an value of 90 degrees the icon will rotate right, a value
     * of -90 will rotate it left, and so on. The maximum accepted rotation 
     * value in either direction is 360.
     * 
     * @param angle the new angle to rotate the icon by
     */
    public void setRotation(float angle) {
        this.angle = (float) Math.toRadians(angle * -1f);
        g.modelMatrix.rotateZ(this.angle);
    }
    
    /**
     * Renders the icon image.
     */
    public void render() {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 4);
        XJGE.getDefaultGLProgram().setUniform("uOpacity", opacity);
        XJGE.getDefaultGLProgram().setUniform("uColor", color.asVec3());
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", currCell);
        XJGE.getDefaultGLProgram().setUniform("uTexture", 0);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    /**
     * Convenience method which frees the data buffers allocated by this class.
     */
    public void freeBuffers() {
        g.freeBuffers();
    }
    
}