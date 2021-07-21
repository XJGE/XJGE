package org.xjge.graphics;

import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

//Created: May 13, 2021

/**
 * Defines a set of reusable vertex data that can be utilized to represent a 2D 
 * object that makes use of instanced rendering techniques.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public class Instance2D {

    public final int vao;
    public final int cellWidth;
    public final int cellHeight;
    
    public final Texture texture;
    public final Atlas atlas;
    
    private final Graphics g = new Graphics();
    
    /**
     * Constructs a rectangular instance that uses a sprite image as its 
     * texture. Useful for text, tiles, etc.
     * 
     * @param texture    the texture image to use
     * @param cellWidth  the width of each sub-image cell in pixels
     * @param cellHeight the height of each sub-image cell in pixels
     * @param fromCenter determines whether the instance will be offset by its 
     *                   center or bottom left corner relative to its position
     */
    public Instance2D(Texture texture, int cellWidth, int cellHeight, boolean fromCenter) {
        this.texture    = texture;
        this.cellWidth  = cellWidth;
        this.cellHeight = cellHeight;
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        vao   = g.vao;
        atlas = new Atlas(texture, cellWidth, cellHeight);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            if(fromCenter) {
                float width  = cellWidth / 2;
                float height = cellHeight / 2;
                
                //(vec3 position), (vec2 texCoords)
                g.vertices.put(-width) .put(height).put(0)  .put(0)                  .put(0);
                g.vertices .put(width) .put(height).put(0)  .put(atlas.subImageWidth).put(0);
                g.vertices .put(width).put(-height).put(0)  .put(atlas.subImageWidth).put(atlas.subImageHeight);
                g.vertices.put(-width).put(-height).put(0)  .put(0)                  .put(atlas.subImageHeight);
            } else {
                //(vec3 position), (vec2 texCoords)
                g.vertices.put(0)        .put(cellHeight).put(0)  .put(0)                  .put(0);
                g.vertices.put(cellWidth).put(cellHeight).put(0)  .put(atlas.subImageWidth).put(0);
                g.vertices.put(cellWidth).put(0)         .put(0)  .put(atlas.subImageWidth).put(atlas.subImageHeight);
                g.vertices.put(0)        .put(0)         .put(0)  .put(0)                  .put(atlas.subImageHeight);
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
    
}