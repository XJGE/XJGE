package dev.theskidster.xjge2.core;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jun 2, 2021
 */

final class FontVertexData {
    
    final int vao;
    private final int vbo;
    private final int ibo;
    private final int vboPosOffset;
    private final int vboTexOffset;
    private final int vboColOffset;
    
    private final FloatBuffer vertices;
    private final IntBuffer indices;
    
    FontVertexData(Font font) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        
        vboPosOffset = glGenBuffers();
        vboTexOffset = glGenBuffers();
        vboColOffset = glGenBuffers();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertices = stack.mallocFloat(20);
            indices  = stack.mallocInt(6);
            
            float subImageWidth  = (float) font.getLargestGlyphWidth() / font.getBitmapWidth();
            float subImageHeight = (float) font.size / font.getBitmapWidth();
            
            //(vec3 position), (vec2 texCoords)
            vertices.put(0)                          .put(font.size).put(0) .put(0)            .put(0);
            vertices.put(font.getLargestGlyphWidth()).put(font.size).put(0) .put(subImageWidth).put(0);
            vertices.put(font.getLargestGlyphWidth()).put(0)        .put(0) .put(subImageWidth).put(subImageHeight);
            vertices.put(0)                          .put(0)        .put(0) .put(0)            .put(subImageHeight);
            
            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);
            
            vertices.flip();
            indices.flip();
        }
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    private void offsetPosition(HashMap<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                positions.put(glyph.position.x).put(glyph.position.y).put(glyph.position.z);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPosOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(4, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);
    }
    
    private void offsetTexture(Font font, HashMap<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer cells = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                cells.put(font.texOffsets.get(glyph.c).x).put(font.texOffsets.get(glyph.c).y);
            });
            
            cells.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboTexOffset);
            glBufferData(GL_ARRAY_BUFFER, cells, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(5, 2, GL_FLOAT, false, (2 * Float.BYTES), 0);
        glEnableVertexAttribArray(5);
        glVertexAttribDivisor(5, 1);
    }
    
    private void offsetColor(HashMap<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colors = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                colors.put(glyph.color.r).put(glyph.color.g).put(glyph.color.b);
            });
            
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboColOffset);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(6, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(6);
        glVertexAttribDivisor(6, 1);
    }
    
    void render(Font font, HashMap<Integer, Glyph> glyphs, boolean changed) {
        XJGE.getDefaultGLProgram().use();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, font.texHandle);
        glBindVertexArray(vao);
        
        if(changed) {
            offsetPosition(glyphs);
            offsetTexture(font, glyphs);
            offsetColor(glyphs);
        }
        
        XJGE.getDefaultGLProgram().setUniform("uType", 1);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, glyphs.size());
        glDisable(GL_BLEND);
        ErrorUtils.checkGLError();
    }
    
    void freeBuffers() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ibo);
    }
    
}