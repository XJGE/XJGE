package org.xjge.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL30.*;
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
 * 
 * @author J Hoffman
 * @since 
 */
public final class TextureReloadable extends Asset {

    private int handle;
    private int width;
    private int height;
    private int channels;

    public TextureReloadable(String filename) {
        super(filename);
    }

    @Override
    protected void onLoad(InputStream stream) {
        try {
            TextureData data = TextureLoader.decode(stream);
            
            // Generate and bind new texture
            handle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, handle);

            // Upload image data
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

            // Set filtering/wrapping
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            // Regenerate mipmaps for updated content
            glGenerateMipmap(GL_TEXTURE_2D);

            // Explicitly flush GL state so reload takes immediate effect
            glBindTexture(GL_TEXTURE_2D, 0);
            glFinish();

            // Free STB-allocated pixel memory (VERY important)
            stbi_image_free(data.pixels);

            width    = data.width;
            height   = data.height;
            channels = data.channels;

            Logger.logInfo("Loaded texture: " + getFilename() + " (" + width + "x" + height + ")");
        } catch(IOException exception) {
            //TODO: use fallback
        }
    }
    
    @Override
    protected void onReload() {
        // Ensure OpenGL immediately uses the new texture data
        Logger.logInfo("Reloaded texture \"" + getFilename() + "\" (handle=" + handle + ")");
        bind();
        glFinish(); // Forces the driver to flush any pending texture operations
        Logger.logInfo("Texture reload applied: " + getFilename());
    }

    @Override
    protected void onRelease() {
        // Optional: keep this empty for reloadable assets
        // We don't want to delete the handle on reload, only on final shutdown
    }

    public void unloadCompletely() {
        if (handle != 0) {
            glDeleteTextures(handle);
            handle = 0;
            Logger.logInfo("Deleted texture: " + getFilename());
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getHandle() { return handle; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

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
            byte[] data = stream.readAllBytes();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer imageBuffer = MemoryUtil.memAlloc(data.length);
                imageBuffer.put(data).flip();

                IntBuffer widthBuffer   = stack.mallocInt(1);
                IntBuffer heightBuffer  = stack.mallocInt(1);
                IntBuffer channelBuffer = stack.mallocInt(1);

                ByteBuffer pixels = stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelBuffer, STBI_rgb_alpha);

                MemoryUtil.memFree(imageBuffer); // ✅ Free upload buffer immediately

                if (pixels == null) {
                    throw new IOException("STBI failed to decode texture: " + stbi_failure_reason());
                }

                return new TextureData(widthBuffer.get(), heightBuffer.get(), channelBuffer.get(), pixels);
            }
        }
    }
    
}