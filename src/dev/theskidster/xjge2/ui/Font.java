package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.Window;
import dev.theskidster.xjge2.core.XJGE;
import dev.theskidster.xjge2.graphics.Graphics;
import dev.theskidster.xjge2.shaderutils.GLProgram;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import org.joml.Vector2f;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackRange;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: May 16, 2021
 */

public final class Font {

    private final int vboPosOffset = glGenBuffers();
    private final int vboTexOffset = glGenBuffers();
    private final int vboColOffset = glGenBuffers();
    private final int texHandle;
    
    private Graphics g;
    
    private HashMap<Character, Vector2f> posOffsets = new HashMap<>();
    private HashMap<Character, Vector2f> texOffsets = new HashMap<>();
    private HashMap<Character, Float> advanceValues = new HashMap<>();
    
    public Font(String filename, int size) {
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        
        try(InputStream file = Font.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
            if(size <= 0) throw new IllegalStateException("Invalid font size " + size + " used.");
            loadFont(file, size);
        } catch(Exception e) {
            Logger.setDomain("ui");
            Logger.logWarning("Failed to load font \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadFont(Font.class.getResourceAsStream("/dev/theskidster/xjge2/assets/fnt_source_code_pro_regular.ttf"), 24);
        }
        
        ErrorUtils.checkGLError();
    }
    
    private void loadFont(InputStream file, int size) {
        /*
        This is a long and complicated process so I'll do my best to explain it
        succinctly here.
        
        The old system was bunk- this new one generates bitmap textures from 
        .ttf files- it needs to go through two stages first before it can be
        utilized properly.
        
        STAGE 1:
        this stage generates a rough bitmap image- it crushes all the quads into
        the single smallest image it can. This would be fine had we not made use
        of instanced rendering...
        
        STAGE 2:
        during this stage we utilize the width of the largest glyph provided by 
        the previous step to determine the length of the quad mesh data that will
        be shared between all glyph instances. The bitmap texture will be 
        regenerated, this time packing glyphs with additional padding to avoid 
        texture bleeding.
        */
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer fontBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
            STBTTFontinfo info = STBTTFontinfo.mallocStack(stack);
            
            if(!stbtt_InitFont(info, fontBuf)) {
                throw new IllegalStateException("Failed to parse font information.");
            }
            
            String charset = " !\"#$%&\'()*+,-./" +
                             "0123456789:;<=>?"   +
                             "@ABCDEFGHIJKLMNO"   +
                             "PQRSTUVWXYZ[\\]^_"  + 
                             "`abcdefghijklmno"   +
                             "pqrstuvwxyz{|}~";
            
            int sizeInPixels = 128;
            int status       = -1;
            int extraCells   = -1;
            
            int imageWidth      = 0;
            int imageHeight     = 0;
            ByteBuffer imageBuf = MemoryUtil.memAlloc(imageWidth * imageHeight);
            STBTTBakedChar.Buffer bakedCharBuf = STBTTBakedChar.malloc(charset.length());
            
            /*
            Here we continuously generate a bitmap image until it contains every
            glyph in the font.
            */
            while(status <= 0) {
                imageWidth  = Math.round(sizeInPixels * Window.getContentScaleX());
                imageHeight = Math.round(sizeInPixels * Window.getContentScaleY());
                imageBuf    = MemoryUtil.memAlloc(imageWidth * imageHeight);
                
                bakedCharBuf = STBTTBakedChar.malloc(charset.length());
                
                extraCells = stbtt_BakeFontBitmap(fontBuf, size * Window.getContentScaleY(), imageBuf, imageWidth, imageHeight, 32, bakedCharBuf);
                status     = Math.abs(extraCells) - charset.length();
                
                if(extraCells > 0) break;
                
                MemoryUtil.memFree(bakedCharBuf);
                MemoryUtil.memFree(imageBuf);
                
                sizeInPixels += 16;
            }
            
            MemoryUtil.memFree(bakedCharBuf);
            int largestGlyphWidth = 0;
            
            for(int i = 0; i < charset.length(); i++) {
                int glyphWidth = Math.round(bakedCharBuf.get(i).xadvance());
                if(glyphWidth > largestGlyphWidth) largestGlyphWidth = glyphWidth;
            }
            
            STBTTPackedchar.Buffer packedCharBuf = STBTTPackedchar.malloc(charset.length());
            STBTTPackRange.Buffer rangeBuf       = STBTTPackRange.malloc(1);
            
            rangeBuf.put(STBTTPackRange.malloc().set(size, 32, null, 96, packedCharBuf, (byte) 1, (byte) 1));
            rangeBuf.flip();
            
            boolean containsAllGlyphs = false;
            
            while(!containsAllGlyphs) {
                imageWidth  = Math.round(sizeInPixels * Window.getContentScaleX());
                imageHeight = Math.round(sizeInPixels * Window.getContentScaleY());
                imageBuf    = MemoryUtil.memAlloc(imageWidth * imageHeight);
                
                try(STBTTPackContext pc = STBTTPackContext.malloc()) {
                    stbtt_PackBegin(pc, imageBuf, imageWidth, imageHeight, 0, largestGlyphWidth, NULL);
                    stbtt_PackSetOversampling(pc, 1, 1);
                    containsAllGlyphs = stbtt_PackFontRange(pc, fontBuf, 0, size, 32, packedCharBuf);
                    stbtt_PackEnd(pc);
                }
                
                if(containsAllGlyphs) break;
                
                MemoryUtil.memFree(imageBuf);
                sizeInPixels += 16;
            }
            
            char[] charArray = charset.toCharArray();
            
            for(int i = 0; i < charset.length(); i++) {
                STBTTPackedchar glyph = packedCharBuf.get(i);
                
                char character = charArray[i];
                float texCoordX  = (float) (glyph.x0()) / imageWidth;
                float texCoordY  = (float) (glyph.y0()) / imageHeight;
                
                posOffsets.put(character, new Vector2f(glyph.xoff(), (glyph.yoff() - glyph.yoff2()) * -1));
                texOffsets.put(character, new Vector2f(texCoordX, texCoordY));
                advanceValues.put(character, glyph.xadvance());
            }
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, imageWidth, imageHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            
            g = new Graphics();
            
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            float subImageWidth  = (float) largestGlyphWidth / imageWidth;
            float subImageHeight = (float) size / imageHeight;
            
            g.vertices.put(0)                .put(size).put(0)  .put(0)            .put(0);
            g.vertices.put(largestGlyphWidth).put(size).put(0)  .put(subImageWidth).put(0);
            g.vertices.put(largestGlyphWidth).put(0).put(0)     .put(subImageWidth).put(subImageHeight);
            g.vertices.put(0)                .put(0).put(0)     .put(0)            .put(subImageHeight);

            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);

            g.vertices.flip();
            g.indices.flip();

            g.bindBuffers();

            glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(2);
            
            MemoryUtil.memFree(fontBuf);
            MemoryUtil.memFree(bakedCharBuf);
            MemoryUtil.memFree(imageBuf);
            
        } catch(IOException e) {
            Logger.setDomain("ui");
            Logger.logSevere("Failed to parse font data.", e);
        }
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
    
    private void offsetTexture(HashMap<Integer, Glyph> glyphs) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer cells = stack.mallocFloat(glyphs.size() * Float.BYTES);
            
            glyphs.forEach((index, glyph) -> {
                cells.put(texOffsets.get(glyph.c).x).put(texOffsets.get(glyph.c).y);
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
    
    float getGlyphAdvance(char c) {
        return advanceValues.get(c);
    }
    
    float getGlyphXOffset(char c) {
        return posOffsets.get(c).x;
    }
    
    float getGlyphDescent(char c) {
        return posOffsets.get(c).y;
    }
    
    void draw(GLProgram program, HashMap<Integer, Glyph> glyphs, boolean changed) {
        program.use();
        
        glBindTexture(GL_TEXTURE_2D, texHandle);
        glBindVertexArray(g.vao);
        
        if(changed) {
            offsetPosition(glyphs);
            offsetTexture(glyphs);
            offsetColor(glyphs);
        }
        
        program.setUniform("uType", 2);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, glyphs.size());
        ErrorUtils.checkGLError();
    }
    
}