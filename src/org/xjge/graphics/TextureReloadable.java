package org.xjge.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.xjge.core.Asset;
import org.xjge.core.AssetManager;
import org.xjge.core.Logger;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public final class TextureReloadable extends Asset {

    private int handle;
    private int width;
    private int height;

    /**
     * Constructs a new texture that will be loaded from the given filename.
     *
     * @param filename the filename of the texture asset
     */
    public TextureReloadable(String filename) {
        super(filename);
    }

    @Override
    protected void onLoad() throws IOException {
        try (InputStream stream = AssetManager.open(filename)) {
            TextureData data = TextureLoader.decode(stream);
            handle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, handle);

            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                data.width,
                data.height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                data.pixels
            );

            // Default filtering and wrapping
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glGenerateMipmap(GL_TEXTURE_2D);

            width = data.width;
            height = data.height;

            glBindTexture(GL_TEXTURE_2D, 0);
            Logger.logInfo("Loaded texture: " + filename + " (" + width + "x" + height + ")");
        } catch (IOException e) {
            Logger.logError("Failed to load texture \"" + filename + "\"", e);
            throw e;
        }
    }

    @Override
    protected void onRelease() {
        if (handle != 0) {
            glDeleteTextures(handle);
            handle = 0;
            Logger.logInfo("Unloaded texture: " + filename);
        }
    }

    /**
     * Binds this texture to the current OpenGL context.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    /**
     * Unbinds any currently bound texture from the current OpenGL context.
     */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getHandle() {
        return handle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Internal texture data representation used for loading.
     */
    private static final class TextureData {
        final int width;
        final int height;
        final int channels;
        final ByteBuffer pixels;

        TextureData(int width, int height, int channels, ByteBuffer pixels) {
            this.width = width;
            this.height = height;
            this.channels = channels;
            this.pixels = pixels;
        }
    }
    
    private static final class TextureLoader {
        static TextureData decode(InputStream stream) throws IOException {
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = stream.readAllBytes();
            
                ByteBuffer imageBuffer  = MemoryUtil.memAlloc(data.length).put(data).flip();
                IntBuffer widthBuffer   = stack.mallocInt(1);
                IntBuffer heightBuffer  = stack.mallocInt(1);
                IntBuffer channelBuffer = stack.mallocInt(1);
                
                ByteBuffer texture = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelBuffer, STBI_rgb_alpha);
                
                if(texture == null) {
                    MemoryUtil.memFree(imageBuffer);
                    throw new RuntimeException("STBI failed to parse texture data: " + stbi_failure_reason());
                }
                
                return new TextureData(widthBuffer.get(), heightBuffer.get(), channelBuffer.get(), texture);
            }
        }
    }
    
}