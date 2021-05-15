package dev.theskidster.xjge2.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: May 13, 2021
 */

public class Instance2D {

    public final int vao;
    public final int cellWidth;
    public final int cellHeight;
    
    public final Texture texture;
    public final Atlas atlas;
    
    private final Graphics g = new Graphics();
    
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