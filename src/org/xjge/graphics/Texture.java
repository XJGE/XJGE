package org.xjge.graphics;

import org.xjge.core.ErrorUtils;
import org.xjge.core.Logger;
import org.xjge.core.XJGE;
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

//Created: May 11, 2021

/**
 * Supplies the data parsed from an image file into a new two-dimensional 
 * texture object that can be used by the graphics pipeline. RBGA encoded .png 
 * is the preferred file format of this engine. OpenGL texture parameters are 
 * expected to be defined outside of this class following the texture objects 
 * initialization.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Texture {
    
    public final int handle;
    private int width;
    private int height;
    private int channels;
    
    /**
     * Creates a new texture object from the image file specified. If the image 
     * file cannot be found, the engine will instead use a fallback texture in 
     * its place.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     * @param target   the OpenGL texture target. One of:
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
    public Texture(String filename, int target) {
        handle = glGenTextures();
        glBindTexture(target, handle);
        
        try(InputStream file = Texture.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
            loadTexture(file, target);
        } catch(Exception e) {
            Logger.setDomain("graphics");
            Logger.logWarning("Failed to load texture \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadTexture(Texture.class.getResourceAsStream("/org/xjge/assets/xjge_missingtexture.png"), target);
        }
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Creates a new texture object from the image file specified. If the image 
     * file cannot be found, the engine will instead use a fallback texture in 
     * its place.
     * 
     * @param filename the name of the file to load. Expects the file extension 
     *                 to be included.
     */
    public Texture(String filename) {
        this(filename, GL_TEXTURE_2D);
    }
    
    /**
     * Parses the data of the image file specified and generates a new OpenGL 
     * texture object from its contents.
     * 
     * @param file   the file to extract texture data from
     * @param target the OpenGL texture target. One of:
     * <table><tr>
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
    
    /**
     * Obtains the width of the texture.
     * 
     * @return the textures width in pixels
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Obtains the height of the texture.
     * 
     * @return the textures height in pixels
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Obtains the amount of color channels this texture uses.
     * 
     * @return the number of color components the texture image exhibits
     */
    public int getChannels() {
        return channels;
    }
    
    /**
     * Frees the OpenGL texture image associated with this object. Should be 
     * used when a texture is no longer needed.
     * 
     * @see org.lwjgl.opengl.GL11#glDeleteTextures(int)
     */
    public void freeTexture() {
        glDeleteTextures(handle);
    }
    
}