package org.xjge.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Asset;
import org.xjge.core.AssetManager;
import org.xjge.core.Logger;

/**
 * Supplies the data parsed from an image file into a new two-dimensional 
 * texture object that can be used by the graphics pipeline. RBGA encoded .png 
 * is the preferred file format of this engine. OpenGL texture parameters are 
 * expected to be defined outside of this class following the texture objects 
 * initialization.
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Texture extends Asset {
    
    private int handle;
    private int target;
    private int width;
    private int height;
    private int channels;
    
    private ByteBuffer pixelBuffer;
    
    public static final Texture FALLBACK = Texture.load("xjge_texture_fallback.png");
    
    public static Texture load(String filename) {
        return AssetManager.load(filename, () -> new Texture(filename, GL_TEXTURE_2D));
    }
    
    private Texture(String filename, int target) {
        super(filename);
        this.target = target;
    }
    
    @Override
    protected void onLoad(InputStream file) {
        handle = glGenTextures();
        glBindTexture(target, handle);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer imageBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer widthBuffer   = stack.mallocInt(1);
            IntBuffer heightBuffer  = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);
            
            pixelBuffer = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelBuffer, STBI_rgb_alpha);
            
            if(pixelBuffer == null) {
                MemoryUtil.memFree(imageBuffer);
                throw new RuntimeException("STBI failed to parse texture data: " + stbi_failure_reason());
            }
            
            width    = widthBuffer.get();
            height   = heightBuffer.get();
            channels = channelBuffer.get();
            
            glTexImage2D(target, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
            
            //stbi_image_free(pixelBuffer);
            MemoryUtil.memFree(imageBuffer);
            
        } catch(IOException exception) {
            Logger.logWarning("Failed to load texture: \"" + getFilename() + "\" a fallback will be used instead", exception);
        }
        
        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        glBindTexture(target, 0);
    }

    @Override
    protected void onRelease() {
        if(this == FALLBACK) return;
        glDeleteTextures(handle);
    }
    
    @Override
    protected Texture onLoadFailure() {
        return FALLBACK;
    }

    /**
     * Binds this texture to the specified target.
     * 
     * @param target the OpenGL texture target. One of:
     * <table><caption></caption><tr>
     * <td>{@link org.lwjgl.opengl.GL11C#GL_TEXTURE_2D TEXTURE_2D}</td>
     * <td>{@link org.lwjgl.opengl.GL30#GL_TEXTURE_1D_ARRAY TEXTURE_1D_ARRAY}</td>
     * <td>{@link org.lwjgl.opengl.GL31#GL_TEXTURE_RECTANGLE TEXTURE_RECTANGLE}</td>
     * <td>{@link org.lwjgl.opengl.GL13#GL_TEXTURE_CUBE_MAP TEXTURE_CUBE_MAP}</td>
     * </tr><tr>
     * <td>{@link org.lwjgl.opengl.GL11C#GL_PROXY_TEXTURE_2D PROXY_TEXTURE_2D}</td>
     * <td>{@link org.lwjgl.opengl.GL30#GL_PROXY_TEXTURE_1D_ARRAY PROXY_TEXTURE_1D_ARRAY}</td>
     * <td>{@link org.lwjgl.opengl.GL31#GL_PROXY_TEXTURE_RECTANGLE PROXY_TEXTURE_RECTANGLE}</td>
     * <td>{@link org.lwjgl.opengl.GL13#GL_PROXY_TEXTURE_CUBE_MAP PROXY_TEXTURE_CUBE_MAP}</td>
     * </tr></table>
     */
    public void bind(int target) {
        this.target = target;
        glBindTexture(target, handle);
    }
    
    public int getWidth() { return width; }
    
    public int getHeight() { return height; }
    
    public int getChannels() { return channels; }
    
    public ByteBuffer getPixelBuffer() { return pixelBuffer; }
    
}