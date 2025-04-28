package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
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
    
    private static final Font2 placeholder = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    
    private Font2(String filepath, String filename, int size) {
        int[] info = loadVectorFont(filepath, filename, size);
        
        isBitmapFont      = info[0] == 1;
        this.size         = info[1];
        textureHandle     = info[2];
        largestGlyphWidth = info[3];
        bitmapWidth       = info[4];
        bitmapHeight      = info[5];
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
            int[] info = new int[6];
            
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
                    //TODO: populate texOffsets, posOffsets, and advanceValues maps.
                }
                
                MemoryUtil.memFree(packedCharBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load font \"" + filename + "\" a placeholder will be used instead", exception);
            
            return new int[] {
                0,
                DEFAULT_SIZE,
                placeholder.textureHandle,
                placeholder.largestGlyphWidth,
                placeholder.bitmapWidth,
                placeholder.bitmapHeight
            };
        }
    }
    
}