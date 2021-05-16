package dev.theskidster.xjge2.ui;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: May 16, 2021
 */

public final class Font {

    public Font(String filename, int size) {
        try(InputStream file = Font.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
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
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer fontBuf = MemoryUtil.memAlloc(data.length).put(data).flip();
            STBTTFontinfo info = STBTTFontinfo.create();
            
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
            
            /*
            Here we continuously generate a bitmap image until it contains every
            glyph in the font.
            */
            while(status <= 0) {
                STBTTBakedChar.Buffer charBuf = STBTTBakedChar.malloc(charset.length());
                ByteBuffer imageBuf           = MemoryUtil.memAlloc(sizeInPixels * sizeInPixels);
                
                int extraCells = stbtt_BakeFontBitmap(fontBuf, size, imageBuf, sizeInPixels, sizeInPixels, 32, charBuf);
                status         = Math.abs(extraCells) - charset.length();
                
                //System.out.println(status);
                
                if(status <= 0) {
                    MemoryUtil.memFree(charBuf);
                    MemoryUtil.memFree(imageBuf);
                    sizeInPixels += 16;
                }
            }
            
            //TODO: add content scale
            //System.out.println(stbtt_BakeFontBitmap(fontBuf, size, ));
            
            
            //free bitmap
            
        } catch(IOException e) {
            Logger.setDomain("ui");
            Logger.logSevere("Failed to parse font data.", e);
        }
    }
    
}