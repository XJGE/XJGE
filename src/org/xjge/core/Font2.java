package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL31C.*;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created: Jun 3, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Font2 {

    private final float SCALE = 1.5f;
    
    private final int FLOATS_PER_GLYPH    = 20;
    private static final int DEFAULT_SIZE = 32;
    
    private final int textureHandle;
    private final int vertexArrayObject;
    private final int vertexBufferObject;
    private final int indicesBufferObject;
    
    public final int size;
    public final int largestGlyphWidth;
    
    private static final Font2 placeholder = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    private final IntBuffer indices;
    
    private final Map<Character, Glyph2> glyphs = new HashMap<>();
    
    private Font2(String filepath, String filename, int size) {
        int[] info = loadVectorFont(filepath, filename, size);
        
        this.size           = info[0];
        textureHandle       = info[1];
        vertexArrayObject   = info[2];
        vertexBufferObject  = info[3];
        indicesBufferObject = info[4];
        largestGlyphWidth   = info[5];
        
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
            int[] info = new int[6];
            
            if(size <= 0 || size > 128) {
                throw new IllegalStateException("Invalid font size used. Font size must be between 1 and 128");
            }
            
            info[0] = size;
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
                
                float pixelScale   = stbtt_ScaleForPixelHeight(fontInfo, info[0]);
                int[] advanceWidth = new int[1];
                int[] leftBearing  = new int[1];
                
                String charset = " !\"#$%&\'()*+,-./" +
                                 "0123456789:;<=>?"   +
                                 "@ABCDEFGHIJKLMNO"   +
                                 "PQRSTUVWXYZ[\\]^_"  + 
                                 "`abcdefghijklmno"   +
                                 "pqrstuvwxyz{|}~";
                
                //Find the width of the largest glyph
                for(int i = 0; i < charset.length(); i++) {
                    stbtt_GetCodepointHMetrics(fontInfo, charset.charAt(i), advanceWidth, leftBearing);
                    float scaledAdvance = advanceWidth[0] * pixelScale;
                    if(scaledAdvance > info[5]) info[5] = Math.round(scaledAdvance * SCALE);
                }
                
                boolean containsAllGlyphs = false;
                int bitmapSizeInPixels    = 128;
                int bitmapWidth           = 0;
                int bitmapHeight          = 0;
                
                ByteBuffer imageBuffer = null;
                STBTTPackedchar.Buffer packedCharBuffer = STBTTPackedchar.malloc(charset.length());
                
                while(!containsAllGlyphs) {
                    bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                    bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                    imageBuffer     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                    
                    try(STBTTPackContext context = STBTTPackContext.malloc()) {
                        stbtt_PackBegin(context, imageBuffer, bitmapWidth, bitmapHeight, 0, (int) (info[5] * SCALE), NULL);
                        stbtt_PackSetOversampling(context, 1, 1);
                        containsAllGlyphs = stbtt_PackFontRange(context, fontBuffer, 0, info[0], 32, packedCharBuffer);
                        stbtt_PackEnd(context);
                    }
                    
                    if(containsAllGlyphs) break;
                    
                    MemoryUtil.memFree(imageBuffer);
                    bitmapSizeInPixels += 16;
                }
                
                glBindTexture(GL_TEXTURE_2D, info[1]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuffer);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glBindTexture(GL_TEXTURE_2D, 0);

                MemoryUtil.memFree(imageBuffer);
                MemoryUtil.memFree(fontBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\" a placeholder will be used instead", exception);
            
            return new int[] {
                DEFAULT_SIZE,
                placeholder.textureHandle,
                placeholder.vertexArrayObject,
                placeholder.vertexBufferObject,
                placeholder.indicesBufferObject,
                placeholder.largestGlyphWidth
            };
        }
    }
    
}