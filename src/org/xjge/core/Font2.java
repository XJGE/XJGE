package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL31C.glDrawElementsInstanced;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.graphics.Color;

/**
 * Created: Jun 3, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Font2 {

    private static final float SCALE          = 1.5f;
    private static final int DEFAULT_SIZE     = 32;
    private static final int FLOATS_PER_GLYPH = 16;
    private static final Font2 placeholder    = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    
    private final int textureHandle;
    private final int vertexArrayObject;
    private final int vertexBufferObject;
    private final int indicesBufferObject;
    
    public final int size;
    
    private final IntBuffer indices;
    private final Map<Character, Glyph2> glyphs = new HashMap<>();
    
    private Font2(String filepath, String filename, int size) {
        int[] info = loadVectorFont(filepath, filename, size);
        
        this.size           = info[0];
        textureHandle       = info[1];
        vertexArrayObject   = info[2];
        vertexBufferObject  = info[3];
        indicesBufferObject = info[4];
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            indices = stack.mallocInt(6);
            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);
            indices.flip();
        }
        
        glBindVertexArray(vertexArrayObject);
        
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glBufferData(GL_ARRAY_BUFFER, FLOATS_PER_GLYPH * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferObject);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    public Font2(String filename, int size) {
        this(XJGE.getAssetsFilepath(), filename, size);
    }
    
    private int[] loadVectorFont(String filepath, String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[5];
            
            if(size <= 0) {
                info[0] = DEFAULT_SIZE;
                Logger.logWarning("Invalid font size used. The font size must be greater than zero", null);
            } else {
                info[0] = size;
            }
            
            info[1] = glGenTextures();
            info[2] = glGenVertexArrays();
            info[3] = glGenBuffers();
            info[4] = glGenBuffers();
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = file.readAllBytes();
                
                ByteBuffer fontBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
                STBTTFontinfo fontInfo = STBTTFontinfo.malloc(stack);
                
                if(!stbtt_InitFont(fontInfo, fontBuffer)) {
                    throw new IllegalStateException("Failed to parse font information from file");
                }
                
                String charset = " !\"#$%&\'()*+,-./" +
                                 "0123456789:;<=>?"   +
                                 "@ABCDEFGHIJKLMNO"   +
                                 "PQRSTUVWXYZ[\\]^_"  + 
                                 "`abcdefghijklmno"   +
                                 "pqrstuvwxyz{|}~";
                
                int bitmapSizeInPixels = 128;
                int exitStatus         = -1;
                int extraCells         = -1;
                int bitmapWidth        = 0;
                int bitmapHeight       = 0;
                
                ByteBuffer imageBuffer = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                STBTTBakedChar.Buffer bakedCharBuffer = STBTTBakedChar.malloc(charset.length());

                /*
                Continuously generate a bitmap image until its large enough to
                contain every glyph in the font. 
                */
                while(exitStatus <= 0) {
                    bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                    bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                    imageBuffer  = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);

                    bakedCharBuffer = STBTTBakedChar.malloc(charset.length());

                    extraCells = stbtt_BakeFontBitmap(fontBuffer, size * SCALE, imageBuffer, bitmapWidth, bitmapHeight, 32, bakedCharBuffer);
                    exitStatus = Math.abs(extraCells) - charset.length();

                    if(extraCells > 0) break;

                    MemoryUtil.memFree(bakedCharBuffer);
                    MemoryUtil.memFree(imageBuffer);

                    bitmapSizeInPixels += 16;
                }
                
                for(int i = 0; i < charset.length(); i++) {
                    STBTTAlignedQuad quad = STBTTAlignedQuad.calloc(stack);
                    
                    FloatBuffer xPositionBuffer = MemoryUtil.memAllocFloat(1);
                    FloatBuffer yPositionBuffer = MemoryUtil.memAllocFloat(1);
                    IntBuffer advanceBuffer     = MemoryUtil.memAllocInt(1);
                    IntBuffer bearingBuffer     = MemoryUtil.memAllocInt(1);
                    
                    stbtt_GetBakedQuad(bakedCharBuffer, bitmapWidth, bitmapHeight, i, xPositionBuffer, yPositionBuffer, quad, true);
                    stbtt_GetGlyphHMetrics(fontInfo, i, advanceBuffer, bearingBuffer);
                    
                    STBTTBakedChar bakedChar = bakedCharBuffer.get(i);
                    
                    Glyph2 glyph = new Glyph2(quad.s0(), quad.s1(), quad.t0(), quad.t1(), 
                                              (int) bakedChar.xadvance(), 
                                              bakedChar.x1() - bakedChar.x0(), 
                                              bakedChar.y1() - bakedChar.y0(), 
                                              (int) bakedChar.xoff(), 
                                              (int) bakedChar.xoff() - (bakedChar.y1() - bakedChar.y0()));
                    
                    glyphs.put(charset.charAt(i), glyph);
                    
                    MemoryUtil.memFree(xPositionBuffer);
                    MemoryUtil.memFree(yPositionBuffer);
                }
                
                MemoryUtil.memFree(bakedCharBuffer);
                
                glBindTexture(GL_TEXTURE_2D, info[1]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuffer);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                
                MemoryUtil.memFree(imageBuffer);
                MemoryUtil.memFree(fontBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\". A placeholder will be used instead", exception);
            
            return new int[] {
                DEFAULT_SIZE,
                placeholder.textureHandle,
                placeholder.vertexArrayObject,
                placeholder.vertexBufferObject,
                placeholder.indicesBufferObject
            };
        }
    }
    
    public void drawString(String text, float positionX, float positionY, Color color, float opacity) {
        XJGE.getDefaultGLProgram().use();
        
        
        
        for(char c : text.toCharArray()) {
            Glyph2 glyph = glyphs.get(c);
            
            float x  = positionX + glyph.bearingX();
            float y  = positionY + glyph.bearingY();
            float w  = glyphs.get(c).width();
            float h  = glyphs.get(c).height();
            float s0 = glyphs.get(c).s0();
            float s1 = glyphs.get(c).s1();
            float t0 = glyphs.get(c).t0();
            float t1 = glyphs.get(c).t1();
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer vertices = stack.mallocFloat(FLOATS_PER_GLYPH);
                
                //(vec2 position), (vec2 texCoords)
                vertices.put(x)    .put(y + h) .put(0) .put(0);
                vertices.put(x + w).put(y + h) .put(s0).put(0);
                vertices.put(x + w).put(y)     .put(s1).put(t0);
                vertices.put(x)    .put(y)     .put(0) .put(t1);
                
                vertices.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            }
            
            positionX += glyph.advance();
        }
        
        glDrawElementsInstanced(GL_TRIANGLES, indices.capacity(), GL_UNSIGNED_INT, 0, glyphs.size());
        glDisable(GL_BLEND);
        
        ErrorUtils.checkGLError();
    }
    
    public int getLength(String text) {
        int length = 0;
        for(char c : text.toCharArray()) length += glyphs.get(c).advance();
        return length;
    }
    
}