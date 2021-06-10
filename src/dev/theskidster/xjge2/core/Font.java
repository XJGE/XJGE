package dev.theskidster.xjge2.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11C.*;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Jun 3, 2021
 */

public final class Font {
    
    final static int DEFAULT_SIZE = 28;
    
    final int texHandle;
    final int size;
    
    private int bitmapWidth;
    private int bitmapHeight;
    private int largestGlyphWidth;
    
    private final float SCALE = 1.5f;
    
    Map<Character, Vector2f> texOffsets = new HashMap<>();
    
    private final HashMap<Character, Vector2f> posOffsets = new HashMap<>();
    private final HashMap<Character, Float> advanceValues = new HashMap<>();
    
    Font() {
        this.size = DEFAULT_SIZE;
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        loadFont(Font.class.getResourceAsStream("/dev/theskidster/xjge2/assets/fnt_debug_mono.ttf"), size);
    }
    
    public Font(String filename, int size) {
        this.size = size;
        
        texHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texHandle);
        
        try(InputStream file = Font.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
            if(size <= 0) throw new IllegalStateException("Invalid font size " + size + " used.");
            loadFont(file, size);
        } catch(Exception e) {
            Logger.setDomain("ui");
            Logger.logWarning("Failed to load font \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadFont(Font.class.getResourceAsStream("/dev/theskidster/xjge2/assets/fnt_debug_mono.ttf"), DEFAULT_SIZE);
        }
        
        ErrorUtils.checkGLError();
    }
    
    private void loadFont(InputStream file, int size) {
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
            
            int bitmapSizeInPixels = 128;
            int exitStatus         = -1;
            int extraCells         = -1;
            
            bitmapWidth  = 0;
            bitmapHeight = 0;
            
            ByteBuffer imageBuf                = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
            STBTTBakedChar.Buffer bakedCharBuf = STBTTBakedChar.malloc(charset.length());
            
            /*
            Continuously generate a bitmap image until its large enough to
            contain every glyph in the font. 
            */
            while(exitStatus <= 0) {
                bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                imageBuf     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                
                bakedCharBuf = STBTTBakedChar.malloc(charset.length());
                
                extraCells = stbtt_BakeFontBitmap(fontBuf, size * SCALE, imageBuf, bitmapWidth, bitmapHeight, 32, bakedCharBuf);
                exitStatus = Math.abs(extraCells) - charset.length();
                
                if(extraCells > 0) break;
                
                MemoryUtil.memFree(bakedCharBuf);
                MemoryUtil.memFree(imageBuf);
                
                bitmapSizeInPixels += 16;
            }
            
            /*
            No guarantee this buffer was freed in the while loop, so we'll just 
            make sure here.
            */
            MemoryUtil.memFree(bakedCharBuf);
            
            largestGlyphWidth = 0;
            
            for(int i = 0; i < charset.length(); i++) {
                int glyphWidth = Math.round(bakedCharBuf.get(i).xadvance());
                if(glyphWidth > largestGlyphWidth) largestGlyphWidth = glyphWidth;
            }
            
            STBTTPackedchar.Buffer packedCharBuf = STBTTPackedchar.malloc(charset.length());
            
            boolean containsAllGlyphs = false;
            
            /*
            Generate a new bitmap that evenly spaces each glyph according to
            the dimensions of the largest one. This extra step helps mitigate  
            texture bleeding.
            */
            while(!containsAllGlyphs) {
                bitmapWidth  = Math.round(bitmapSizeInPixels * SCALE);
                bitmapHeight = Math.round(bitmapSizeInPixels * SCALE);
                imageBuf     = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
                
                try(STBTTPackContext pc = STBTTPackContext.malloc()) {
                    stbtt_PackBegin(pc, imageBuf, bitmapWidth, bitmapHeight, 0, (int) (largestGlyphWidth * SCALE), NULL);
                    stbtt_PackSetOversampling(pc, 1, 1);
                    containsAllGlyphs = stbtt_PackFontRange(pc, fontBuf, 0, size, 32, packedCharBuf);
                    stbtt_PackEnd(pc);
                }
                
                if(containsAllGlyphs) break;
                
                MemoryUtil.memFree(imageBuf);
                bitmapSizeInPixels += 16;
            }
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmapWidth, bitmapHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            
            MemoryUtil.memFree(imageBuf);
            MemoryUtil.memFree(fontBuf);
            
            //Store glyph data in a format that we can use.
            for(int i = 0; i < charset.length(); i++) {
                STBTTPackedchar glyph = packedCharBuf.get(i);
                
                char character  = charset.charAt(i);
                float texCoordX = (float) (glyph.x0()) / bitmapWidth;
                float texCoordY = (float) (glyph.y0()) / bitmapHeight;
                
                texOffsets.put(character, new Vector2f(texCoordX, texCoordY));
                posOffsets.put(character, new Vector2f(glyph.xoff(), -glyph.yoff() - size));
                advanceValues.put(character, glyph.xadvance());
            }
            
            texOffsets = Collections.unmodifiableMap(texOffsets);
            
            MemoryUtil.memFree(packedCharBuf);
            
        } catch(IOException e) {
            Logger.setDomain("core");
            Logger.logSevere("Failed to parse font data.", e);
        }
    }
    
    int getBitmapWidth() {
        return bitmapWidth;
    }
    
    int getBitmapHeight() {
        return bitmapHeight;
    }
    
    int getLargestGlyphWidth() {
        return largestGlyphWidth;
    }
    
    float getGlyphAdvance(char c) {
        return advanceValues.get(c);
    }
    
    float getGlyphBearing(char c) {
        return posOffsets.get(c).x;
    }
    
    float getGlyphDescent(char c) {
        return posOffsets.get(c).y;
    }
    
    public void freeTexture() {
        glDeleteTextures(texHandle);
    }
    
}