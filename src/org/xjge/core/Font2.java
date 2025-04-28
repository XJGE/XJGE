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

    private final float SCALE = 1.5f;
    
    private static final int DEFAULT_SIZE = 32;
    
    private final int textureHandle;
    
    public final int size;
    public final int largestGlyphWidth;
    
    private static final Font2 placeholder = new Font2("/org/xjge/assets/", "font_source_code_pro.ttf", DEFAULT_SIZE);
    
    private Font2(String filepath, String filename, int size) {
        int[] info = loadVectorFont(filepath, filename, size);
        
        this.size           = info[0];
        textureHandle       = info[1];
        largestGlyphWidth   = info[2];
    }
    
    public Font2(String filename, int size) {
        this(XJGE.getAssetsFilepath(), filename, size);
    }
    
    private int[] loadVectorFont(String filepath, String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[3];
            
            if(size <= 0 || size > 128) {
                throw new IllegalStateException("Invalid font size used. Font size must be between 1 and 128");
            }
            
            info[0] = size;
            info[1] = glGenTextures();
            
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
                    if(scaledAdvance > info[2]) info[2] = Math.round(scaledAdvance * SCALE);
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
                    imageBuffer  = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                    
                    try(STBTTPackContext context = STBTTPackContext.malloc()) {
                        stbtt_PackBegin(context, imageBuffer, bitmapWidth, bitmapHeight, 0, (int) (info[2] * SCALE), NULL);
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
                placeholder.largestGlyphWidth
            };
        }
    }
    
}