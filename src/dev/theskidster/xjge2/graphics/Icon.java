package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
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
 * @see dev.theskidster.xjge2.core.Widget
 */
public final class Icon {

    private Vector2f currCell = new Vector2f();
    private final Graphics g  = new Graphics();
    private final Texture texture;
    private final Atlas atlas;
    
    /**
     * Creates a new icon object which can be used to comprise part of a larger 
     * user interface.
     * 
     * @param texture    the texture image to use
     * @param cellWidth  the width of each sub-image cell in pixels
     * @param cellHeight the height of each sub-image cell in pixels
     */
    public Icon(Texture texture, int cellWidth, int cellHeight) {
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
            
            //(vec3 position), (vec2 tex coords)
            g.vertices.put(0)              .put(atlas.cellHeight).put(0)    .put(0)                  .put(0);
            g.vertices.put(atlas.cellWidth).put(atlas.cellHeight).put(0)    .put(atlas.subImageWidth).put(0);
            g.vertices.put(atlas.cellWidth).put(0)               .put(0)    .put(atlas.subImageWidth).put(atlas.subImageHeight);
            g.vertices.put(0)              .put(0)               .put(0)    .put(0)                  .put(atlas.subImageHeight);
            
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
     * Sets the current position of the icon on the screen
     * 
     * @param position the position to set this icon to
     */
    public void setPosition(Vector2i position) {
        g.modelMatrix.translation(position.x, position.y, 0);
    }
    
    /**
     * Variant of {@link setPosition(Vector2i)}.
     * 
     * @param x the x position coordinate to set the icon to
     * @param y the y position coordinate to set the icon to
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
                              ", " + cellY + ") is out of bounds.",
                              null);
            Logger.setDomain(null);
        }
    }
    
    /**
     * Renders the icon image.
     */
    public void render() {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        XJGE.getDefaultGLProgram().setUniform("uType", 4);
        XJGE.getDefaultGLProgram().setUniform("uModel", false, g.modelMatrix);
        XJGE.getDefaultGLProgram().setUniform("uTexCoords", currCell);
        
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