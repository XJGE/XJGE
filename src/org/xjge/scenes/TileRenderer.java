package org.xjge.scenes;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;
import org.xjge.core.ErrorUtils;
import org.xjge.graphics.Atlas;
import org.xjge.graphics.GLProgram;
import org.xjge.graphics.Graphics;
import org.xjge.graphics.Texture;

/**
 * Dec 27, 2021
 */

/**
 * @author J Hoffman
 * @since  
 */
public class TileRenderer {

    public static final int TILE_SIZE = 16;
    
    private final int vboPosOffset = glGenBuffers();
    private final int vboTexOffset = glGenBuffers();
    
    private Texture tileTexture;
    private Atlas texAtlas;
    private Graphics g;
    
    private Matrix3f normal = new Matrix3f();
    
    private Map<Integer, Vector2f> texOffsets = new HashMap<>();
    
    TileRenderer() {
        tileTexture = new Texture("spr_tileset.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        texAtlas = new Atlas(tileTexture, TILE_SIZE, TILE_SIZE);
        
        float texPosX = 0f;
        float texPosY = 0f;
        
        for(int tileID = 1; tileID < texAtlas.subImageCount + 1; tileID++) {
            if(tileID % texAtlas.rows != 1 && tileID != 1) {
                texPosX += texAtlas.subImageWidth;
                texOffsets.put(tileID, new Vector2f(texPosX, texPosY));
            } else if(tileID == 1) {
                texOffsets.put(tileID, new Vector2f(texPosX, texPosY));
            } else {
                texPosX = 0f;
                texPosY += texAtlas.subImageHeight;
                texOffsets.put(tileID, new Vector2f(texPosX, texPosY));
            }
        }
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            float halfWidth  = TILE_SIZE / 2;
            float halfHeight = TILE_SIZE / 2;
            
            //(vec3 position), (vec2 texCoords)
            g.vertices.put(-halfWidth).put(halfHeight) .put(0)  .put(0)                     .put(0);
            g.vertices.put(halfWidth) .put(halfHeight) .put(0)  .put(texAtlas.subImageWidth).put(0);
            g.vertices.put(halfWidth) .put(-halfHeight).put(0)  .put(texAtlas.subImageWidth).put(texAtlas.subImageHeight);
            g.vertices.put(-halfWidth).put(-halfHeight).put(0)  .put(0)                     .put(texAtlas.subImageHeight);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    private void offsetPosition(Map<Vector2i, Tile> tiles) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(tiles.size() * Float.BYTES);
            
            tiles.values().forEach((tile) -> {
                positions.put(tile.position.x).put(tile.position.y).put(tile.position.z);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPosOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(4, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);
    }
    
    private void offsetTexture(Map<Vector2i, Tile> tiles) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer cells = stack.mallocFloat(tiles.size() * Float.BYTES);
            
            tiles.forEach((location, tile) -> {
                cells.put(texOffsets.get(tile.id).x).put(texOffsets.get(tile.id).y);
            });
            
            cells.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboTexOffset);
            glBufferData(GL_ARRAY_BUFFER, cells, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(5, 2, GL_FLOAT, false, (2 * Float.BYTES), 0);
        glEnableVertexAttribArray(5);
        glVertexAttribDivisor(5, 1);
    }
    
    void draw(GLProgram gameProgram, Map<Vector2i, Tile> tiles, int depthTexHandle) {
        gameProgram.use();
        
        glEnable(GL_DEPTH_TEST);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tileTexture.handle);
        
        //glActiveTexture(GL_TEXTURE1);
        //glBindTexture(GL_TEXTURE_2D, depthTexHandle);
        
        glBindVertexArray(g.vao);
        
        offsetPosition(tiles);
        offsetTexture(tiles);
        
        gameProgram.setUniform("uType", 11);
        gameProgram.setUniform("uTexture", 0);
        //gameProgram.setUniform("uShadowMap", 1);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, tiles.size());
        
        glDisable(GL_DEPTH_TEST);
        
        ErrorUtils.checkGLError();
    }
    
}