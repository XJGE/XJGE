package dev.theskidster.xjge2.graphics;

import dev.theskidster.xjge2.core.ErrorUtils;
import dev.theskidster.xjge2.core.Logger;
import dev.theskidster.xjge2.core.XJGE;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: May 11, 2021
 */

public final class Texture {
    
    public final int handle;
    private int width;
    private int height;
    private int channels;
    
    public Texture(String filename, int target) {
        handle = glGenTextures();
        glBindTexture(target, handle);
        
        try(InputStream file = Texture.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            loadTexture(file, target);
        } catch(Exception e) {
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to load texture \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadTexture(Texture.class.getResourceAsStream("/dev/theskidster/xjge2/assets/img_null.png"), target);
        }
        
        ErrorUtils.checkGLError();
    }
    
    public Texture(String filename) {
        this(filename, GL_TEXTURE_2D);
    }
    
    private void loadTexture(InputStream file, int target) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer imageBuf  = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer texture = stbi_load_from_memory(imageBuf, widthBuf, heightBuf, channelBuf, STBI_rgb_alpha);
            
            width    = widthBuf.get();
            height   = heightBuf.get();
            channels = channelBuf.get();
            
            if(texture != null) {
                glTexImage2D(target, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
            } else {
                throw new NullPointerException("STBI failed to parse texture image data.");
            }
            
            stbi_image_free(texture);
            MemoryUtil.memFree(imageBuf);
            
        } catch(IOException e) {
            Logger.setDomain("graphics");
            Logger.logSevere("Failed to parse texture image data.", e);
        }
    }
    
    public int getWidth()    { return width; }
    public int getHeight()   { return height; }
    public int getChannels() { return channels; }
    
    public void freeTexture() {
        glDeleteTextures(handle);
    }
    
}