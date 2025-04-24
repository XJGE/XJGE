package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_GetBakedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * Created: Jun 3, 2021
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Font2 {

    static final int DEFAULT_SIZE = 32;
    
    private final int textureHandle;
    private final int vertexArrayObject;
    private final int vertexBufferObject;
    
    public final int size;
    
    static final float SCALE = 1.5f;
    
    private static final Font2 missingFont = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    
    private final Map<Character, Glyph2> glyphs = new HashMap<>();
    
    private Font2(String filepath, String filename, int size) {
        this.size = size;
        
        int[] info = loadVectorFont(filepath, filename);
        
        textureHandle      = info[0];
        vertexArrayObject  = info[1];
        vertexBufferObject = info[2];
    }
    
    public Font2(String filename, int size) {
        this(XJGE.getAssetsFilepath(), filename, size);
    }
    
    private int[] loadBitmapFont() {
        return null;
    }
    
    private int[] loadVectorFont(String filepath, String filename) {
        try(InputStream file = Font.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[4];
            
            info[0] = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, info[0]);
            
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
                    
                    Glyph2 glyph = new Glyph2();
                    STBTTBakedChar bakedChar = bakedCharBuffer.get(i);
                    
                    glyph.advance  = (int) bakedChar.xadvance();
                    glyph.width    = (bakedChar.x1() - bakedChar.x0());
                    glyph.height   = (bakedChar.y1() - bakedChar.y0());
                    glyph.bearingX = (int) bakedChar.xoff();
                    glyph.bearingY = (int) (-bakedChar.yoff() - glyph.height);
                    glyph.s0       = quad.s0();
                    glyph.s1       = quad.s1();
                    glyph.t0       = quad.t0();
                    glyph.t1       = quad.t1();
                    
                    glyphs.put(charset.charAt(i), glyph);
                    
                    MemoryUtil.memFree(xPositionBuffer);
                    MemoryUtil.memFree(yPositionBuffer);
                }
                
                MemoryUtil.memFree(bakedCharBuffer);
                
                glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuffer);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                
                MemoryUtil.memFree(imageBuffer);
                MemoryUtil.memFree(fontBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\". A placeholder will be used instead.", exception);
            
            return new int[] {
                missingFont.textureHandle,
                missingFont.vertexArrayObject,
                missingFont.vertexBufferObject
            };
        } 
    }
    
    public static int getLengthInPixels(String text) {
        return 0;
    }
    
}