package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

    final boolean isBitmapFont;
    
    private final float SCALE = 1.5f;
    
    private static final int DEFAULT_SIZE = 32;
    
    final int size;
    final int textureHandle;
    final int largestGlyphWidth;
    final int bitmapWidth;
    final int bitmapHeight;
    
    private final int vaoHandle;
    private final int vboHandle;
    private final int iboHandle;
    private final int posOffsetHandle;
    private final int texOffsetHandle;
    private final int colOffsetHandle;
    
    private final FloatBuffer vertexBuffer;
    private final IntBuffer indexBuffer;
    
    private static final Font2 placeholder = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    
    private final Map<Character, GlyphMetrics> glyphMetrics = new HashMap<>();
    
    private record GlyphMetrics (
        float texCoordX,
        float texCoordY,
        int advance,
        int leftBearing,
        int descent
    ) {}
    
    private Font2(String filepath, String filename, int size) {
        int[] info = loadVectorFont(filepath, filename, size);
        
        isBitmapFont      = info[0] == 1;
        this.size         = info[1];
        textureHandle     = info[2];
        largestGlyphWidth = info[3];
        bitmapWidth       = info[4];
        bitmapHeight      = info[5];
        vaoHandle         = info[6];
        vboHandle         = info[7];
        iboHandle         = info[8];
        posOffsetHandle   = info[9];
        texOffsetHandle   = info[10];
        colOffsetHandle   = info[11];
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertexBuffer = stack.mallocFloat(20);
            indexBuffer  = stack.mallocInt(6);
            
            float subImageWidth  = (float) largestGlyphWidth / bitmapWidth;
            float subImageHeight = (float) this.size / bitmapHeight;
            
            //(vec3 position), (vec2 texCoords)
            vertexBuffer.put(0)                .put(this.size).put(0)   .put(0)            .put(0);
            vertexBuffer.put(largestGlyphWidth).put(this.size).put(0)   .put(subImageWidth).put(0);
            vertexBuffer.put(largestGlyphWidth).put(0)        .put(0)   .put(subImageWidth).put(subImageHeight);
            vertexBuffer.put(0)                .put(0)        .put(0)   .put(0)            .put(subImageHeight);
            
            indexBuffer.put(0).put(1).put(2);
            indexBuffer.put(2).put(3).put(0);
            
            vertexBuffer.flip();
            indexBuffer.flip();
        }
        
        glBindVertexArray(vaoHandle);
        
        glBindBuffer(GL_ARRAY_BUFFER, vboHandle);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    public Font2(String filename, int size) {
        this(XJGE.getAssetsFilepath(), filename, size);
    }
    
    private int[] loadBitmapFont(String filepath, String filename, int size) {
        //TODO: reimplement .bmf file parsing
        return null;
    }
    
    private int[] loadVectorFont(String filepath, String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[12];
            
            if(size <= 0 || size > 128) {
                throw new IllegalStateException("Invalid font size used. Font size must be between 1 and 128");
            }
            
            info[0] = 0;
            info[1] = size;
            info[2] = glGenTextures();
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = file.readAllBytes();
                
                ByteBuffer fontBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
                STBTTFontinfo fontInfo = STBTTFontinfo.malloc(stack);
                
                if(!stbtt_InitFont(fontInfo, fontBuffer)) {
                    throw new IllegalStateException("Failed to parse font information from file");
                }
                
                float pixelScale   = stbtt_ScaleForPixelHeight(fontInfo, info[1]);
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
                    if(scaledAdvance > info[3]) info[3] = Math.round(scaledAdvance * SCALE);
                }
                
                boolean containsAllGlyphs = false;
                int bitmapSizeInPixels    = 128;
                
                ByteBuffer imageBuffer = null;
                STBTTPackedchar.Buffer packedCharBuffer = STBTTPackedchar.malloc(charset.length());
                
                /*
                Generate a bitmap that evenly spaces each glyph according to the
                dimensions of the largest one. This mitigates texture bleeding.
                */
                while(!containsAllGlyphs) {
                    info[4]     = Math.round(bitmapSizeInPixels * SCALE);
                    info[5]     = Math.round(bitmapSizeInPixels * SCALE);
                    imageBuffer = MemoryUtil.memAlloc(info[4] * info[5]);
                    
                    try(STBTTPackContext context = STBTTPackContext.malloc()) {
                        stbtt_PackBegin(context, imageBuffer, info[4], info[5], 0, (int) (info[3] * SCALE), NULL);
                        stbtt_PackSetOversampling(context, 1, 1);
                        containsAllGlyphs = stbtt_PackFontRange(context, fontBuffer, 0, info[1], 32, packedCharBuffer);
                        stbtt_PackEnd(context);
                    }
                    
                    if(containsAllGlyphs) break;
                    
                    MemoryUtil.memFree(imageBuffer);
                    bitmapSizeInPixels += 16;
                }
                
                glBindTexture(GL_TEXTURE_2D, info[2]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, info[4], info[5], 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuffer);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glBindTexture(GL_TEXTURE_2D, 0);

                MemoryUtil.memFree(imageBuffer);
                MemoryUtil.memFree(fontBuffer);
                
                //Store glyph data in a format the engine can use
                for(int i = 0; i < charset.length(); i++) {
                    STBTTPackedchar glyph = packedCharBuffer.get(i);
                    GlyphMetrics metrics  = new GlyphMetrics((float) (glyph.x0()) / info[4],
                                                             (float) (glyph.y0()) / info[5],
                                                             (int) glyph.xadvance(),
                                                             (int) glyph.xoff(), 
                                                             (int) (-glyph.yoff() - info[1]));
                    
                    glyphMetrics.put(charset.charAt(i), metrics);
                }
                
                MemoryUtil.memFree(packedCharBuffer);
            }
            
            //Initialize various OpenGL objects
            info[6] = glGenVertexArrays();
            for(int i = 7; i < 12; i++) info[i] = glGenBuffers();
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\" a placeholder will be used instead", exception);
            
            return new int[] {
                0,
                DEFAULT_SIZE,
                placeholder.textureHandle,
                placeholder.largestGlyphWidth,
                placeholder.bitmapWidth,
                placeholder.bitmapHeight,
                placeholder.vaoHandle,
                placeholder.vboHandle,
                placeholder.iboHandle,
                placeholder.posOffsetHandle,
                placeholder.texOffsetHandle,
                placeholder.colOffsetHandle
            };
        }
    }
    
    private float getGlyphTexCoordX(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).texCoordX;
    }
    
    private float getGlyphTexCoordY(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).texCoordY;
    }
    
    private int getGlyphAdvance(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).advance;
    }
    
    private int getGlyphLeftBearing(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).leftBearing;
    }
    
    private int getGlyphDescent(char character) {
        return glyphMetrics.get((!glyphMetrics.containsKey(character) ? '?' : character)).descent;
    }
    
    public void delete() {
        if(textureHandle != placeholder.textureHandle) glDeleteTextures(textureHandle);
    }
    
}