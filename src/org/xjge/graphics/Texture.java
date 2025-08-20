package org.xjge.graphics;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Logger;
import org.xjge.core.XJGE;

/**
 * Created: May 11, 2021
 * <br><br>
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

    private final int handle;
    
    public final int width;
    public final int height;
    public final int channels;
    
    public static final Texture fallback = new Texture("/org/xjge/assets/", "xjge_texture_fallback.png", GL_TEXTURE_2D);
    
    /**
     * Creates a new texture object from the image file specified. If the image 
     * file cannot be found, the engine will use a placeholder texture instead.
     * 
     * @param filename the name of the file to load (with extension)
     */
    public Texture(String filename) {
        this(XJGE.getAssetsFilepath(), filename, GL_TEXTURE_2D);
    }
    
    /**
     * Creates a new texture object from the image file specified. If the image 
     * file cannot be found, the engine will use a placeholder texture instead.
     * 
     * @param filename the name of the file to load (with extension)
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
    public Texture(String filename, int target) {
        this(XJGE.getAssetsFilepath(), filename, target);
    }
    
    /**
     * Default constructor used internally to load the placeholder texture.
     */
    private Texture(String filepath, String filename, int target) {
        int[] info = loadTexture(filepath, filename, target);
        
        handle   = info[0];
        width    = info[1];
        height   = info[2];
        channels = info[3];
        
        bind(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    /**
     * Parses data from the specified image file and generates a new OpenGL 
     * texture object.
     * 
     * @param filename the name of the file to load (with extension)
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
     * @return an array of integers containing data parsed from the image file
     */
    private int[] loadTexture(String filepath, String filename, int target) {
        try(InputStream file = Texture.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[4];
            
            info[0] = glGenTextures();
            glBindTexture(target, info[0]);
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = file.readAllBytes();
                
                ByteBuffer imageBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
                IntBuffer widthBuffer   = stack.mallocInt(1);
                IntBuffer heightBuffer  = stack.mallocInt(1);
                IntBuffer channelBuffer = stack.mallocInt(1);
                
                ByteBuffer texture = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelBuffer, STBI_rgb_alpha);
                
                if(texture == null) {
                    MemoryUtil.memFree(imageBuffer);
                    throw new RuntimeException("STBI failed to parse texture image data: " + stbi_failure_reason());
                }
                
                info[1] = widthBuffer.get();
                info[2] = heightBuffer.get();
                info[3] = channelBuffer.get();
                
                glTexImage2D(target, 0, GL_RGBA, info[1], info[2], 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);

                stbi_image_free(texture);
                MemoryUtil.memFree(imageBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load texture \"" + filename + "\" a fallback will be used instead", exception);
            
            return new int[] {
                fallback.handle,
                fallback.width,
                fallback.height,
                fallback.channels
            };
        }
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
        glBindTexture(target, handle);
    }
    
    /**
     * Frees the OpenGL texture associated with this object and removes it from 
     * the systems VRAM.
     * 
     * @see org.lwjgl.opengl.GL11#glDeleteTextures(int)
     */
    public void delete() {
        if(handle != fallback.handle) glDeleteTextures(handle);
    }
    
}