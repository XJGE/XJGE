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
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
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

    private final int texHandle;
    private Graphics g;
    
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
            int extraCells   = -1;
            
            int imageWidth      = 0;
            int imageHeight     = 0;
            ByteBuffer imageBuf = MemoryUtil.memAlloc(imageWidth * imageHeight);
            STBTTBakedChar.Buffer charBuf = STBTTBakedChar.malloc(charset.length());
            
            /*
            Here we continuously generate a bitmap image until it contains every
            glyph in the font.
            */
            while(status <= 0) {
                imageWidth  = Math.round(sizeInPixels * Window.getContentScaleX());
                imageHeight = Math.round(sizeInPixels * Window.getContentScaleY());
                imageBuf    = MemoryUtil.memAlloc(imageWidth * imageHeight);
                
                charBuf = STBTTBakedChar.malloc(charset.length());
                
                extraCells = stbtt_BakeFontBitmap(fontBuf, size * Window.getContentScaleY(), imageBuf, imageWidth, imageHeight, 32, charBuf);
                status     = Math.abs(extraCells) - charset.length();
                
                if(extraCells > 0) {
                    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, imageWidth, imageHeight, 0, GL_ALPHA, GL_UNSIGNED_BYTE, imageBuf);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                    break;
                }
                
                MemoryUtil.memFree(charBuf);
                MemoryUtil.memFree(imageBuf);
                
                sizeInPixels += 16;
            }
            
            g = new Graphics();
            
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);

            g.vertices.put(-imageWidth) .put(imageHeight).put(0)  .put(0).put(0);
            g.vertices .put(imageWidth) .put(imageHeight).put(0)  .put(1).put(0);
            g.vertices .put(imageWidth).put(-imageHeight).put(0)  .put(1).put(1);
            g.vertices.put(-imageWidth).put(-imageHeight).put(0)  .put(0).put(1);

            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);

            g.vertices.flip();
            g.indices.flip();

            g.bindBuffers();

            glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(2);
            
            char[] charArray = charset.toCharArray();
            
            for(int i = 0; i < charset.length(); i++) {
                STBTTBakedChar glyph = charBuf.get(i);
                
                float xStart  = (float) (glyph.x0()) / imageWidth;
                float yStart  = (float) (glyph.y0()) / imageHeight;
                float xEnd    = (float) (glyph.x1()) / imageWidth;
                float yEnd    = (float) (glyph.y1()) / imageHeight;
                float advance = glyph.xadvance();
                
                /*
                TODO:
                
                the quad each glyph will be rendering to is likely going to be
                larger than the visible character itself- maybe we can use the 
                xEnd and yEnd coordinates to discard fragments that exend beyond
                this point?
                
                The worry here of course is that the rendered glyph will contain
                other parts of glyphs in the bitmap image.
                
                include the data parsed here into collections and start rendering
                strings, fix whatever issues arise after
                */
                
                System.out.println("char: " + charArray[i]);
                System.out.println("tex start X: " + xStart);
                System.out.println("tex start Y: " + yStart);
                System.out.println("tex end X: " + xEnd);
                System.out.println("tex end Y: " + yEnd);
                System.out.println("off X: " + glyph.xoff());
                System.out.println("off Y: " + glyph.yoff());
                System.out.println("advance: " + advance); //monospaced fonts will all have the same value here.
                System.out.println();
            }
            
            /*
            use the largest advance value to determine the glyph quad size.
            */
            
            MemoryUtil.memFree(fontBuf);
            MemoryUtil.memFree(charBuf);
            MemoryUtil.memFree(imageBuf);
            
        } catch(IOException e) {
            Logger.setDomain("ui");
            Logger.logSevere("Failed to parse font data.", e);
        }
    }
    
    private Vector3f pos = new Vector3f(0, -100, -1000);
    
    public void update() {
        g.modelMatrix.translation(pos);
    }
    
    public void render(GLProgram program) {
        program.use();
        
        glBindTexture(GL_TEXTURE_2D, texHandle);
        glBindVertexArray(g.vao);
        
        program.setUniform("uType", 2);
        program.setUniform("uModel", false, g.modelMatrix);
        
        glDrawElements(GL_TRIANGLES, g.indices.capacity(), GL_UNSIGNED_INT, 0);
        
        ErrorUtils.checkGLError();
    }
    
}