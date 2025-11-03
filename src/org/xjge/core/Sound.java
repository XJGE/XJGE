package org.xjge.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
public final class Sound extends Asset {
    
    private float durationInSeconds;
    
    private int handle; 
    private int channels;
    private int frequency;
    
    public static final Sound FALLBACK = Sound.load("xjge_sound_fallback.ogg");
    
    public static Sound load(String filename) {
        return AssetManager.load(filename, () -> new Sound(filename));
    }
    
    Sound(String filename) {
        super(filename);
    }
    
    @Override
    protected void onLoad(InputStream file) {
        handle = alGenBuffers();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer soundBuffer   = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleBuffer   = stack.mallocInt(1);
            
            ShortBuffer sound = stb_vorbis_decode_memory(soundBuffer, channelsBuffer, sampleBuffer);
            
            if(sound == null) {
                MemoryUtil.memFree(soundBuffer);
                throw new IOException(stbi_failure_reason());
            }
            
            channels  = channelsBuffer.get();
            frequency = sampleBuffer.get();
            
            durationInSeconds = (float) sound.capacity() / (channels * frequency);
            
            alBufferData(handle, (channels == 1) ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, sound, frequency);
            MemoryUtil.memFree(soundBuffer);
            
        } catch(IOException exception) {
            Logger.logWarning("Failed to load sound: \"" + getFilename() + "\" a fallback will be used instead", exception);
        }
    }

    @Override
    protected void onReload() {
        Logger.logInfo("Sound file: \"" + getFilename() + "\" reloaded successfully");
        
        var source = AudioSystem.getSourceWithSound(getFilename());
        if(source != null) source.recoverSounds();
    }

    @Override
    protected void onRelease() {
        alDeleteBuffers(handle);
    }
    
    @Override
    protected Sound onLoadFailure() {
        return FALLBACK;
    }
    
    int getHandle() { return handle; }
    
    int getFrequency() { return frequency; }
    
    public float getDurationInSeconds() { return durationInSeconds; }
    
}