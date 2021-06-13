package dev.theskidster.xjge2.core;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Atlas;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 8, 2021
 */

public final class Icon {

    private Vector2f currCell = new Vector2f();
    private final Graphics g  = new Graphics();
    private final Texture texture;
    private final Atlas atlas;
    
    public Icon(Texture texture, int cellWidth, int cellHeight) {
        this.texture = texture;
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
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
    
    public void setPosition(Vector3f position) {
        g.modelMatrix.translation(position);
    }
    
    public void setPosition(float x, float y, float z) {
        g.modelMatrix.translation(x, y, z);
    }
    
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
    
    public void freeBuffers() {
        g.freeBuffers();
    }
    
}