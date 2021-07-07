package dev.theskidster.xjge2.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 13, 2021
 */

/**
 * A Cubemap is similar in function to a {@link Texture} object, but instead uses the data supplied through multiple image files to generate a new
 * three-dimensional texture object that can be used to map several individual images onto different faces of a mesh.
 */
final class Cubemap {

    final int handle;
    
    /**
     * Creates a new cubemap texture object from the image files provided. Like conventional {@link Texture} objects, the cubemap will provide a
     * fallback texture in place of missing images. 
     * <br><br>
     * <i>All images used by the cubemap must exhibit the same width/height dimensions in pixels otherwise it will appear black.</i>
     * 
     * @param images the names of each image file to parse texture data from
     */
    Cubemap(Map<Integer, String> images) {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, handle);
        
        images.forEach((target, filename) -> {
            try(InputStream file = Cubemap.class.getResourceAsStream(XJGE.getAssetsFilepath() + filename)) {
                loadCubemapTexture(target, file);
            } catch(Exception e) {
                Logger.setDomain("graphics");
                Logger.logWarning("Failed to load cubemap texture \"" + filename + "\"", e);
                Logger.setDomain(null);
                
                loadCubemapTexture(target, Cubemap.class.getResourceAsStream("/dev/theskidster/xjge2/assets/img_null.png"));
            }
        });
        
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        ErrorUtils.checkGLError();
    }
    
    /**
     * Parses the data of the image file specified and uses it to generate a new texture the cubemap can use.
     * 
     * @param target the OpenGL texture target, this is specified automatically through the cubemaps constructor
     * @param file   the file to parse texture data from
     */
    private void loadCubemapTexture(int target, InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer imageBuf  = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer texture = stbi_load_from_memory(imageBuf, widthBuf, heightBuf, channelBuf, STBI_rgb_alpha);
            
            if(texture != null) {
                glTexImage2D(target, 0, GL_RGBA, widthBuf.get(), heightBuf.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
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
     * Frees the OpenGL texture object used by this class.
     */
    void freeTexture() {
        glDeleteTextures(handle);
    }
    
}