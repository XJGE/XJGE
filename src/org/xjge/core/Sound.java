package org.xjge.core;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * 
 * @author J Hoffman
 * @since 2.0.0
 */
final class Sound {
    
    final float durationInSeconds;
    
    final int handle; 
    final int channels;
    final int frequency;
    
    final String filepath;
    final String filename;
    
    static Sound fallback = new Sound(XJGE.ASSETS_FILEPATH, "xjge_sound_fallback.ogg");
    
    Sound(String filepath, String filename) {
        this.filepath = filepath;
        this.filename = filename;
        
        int[] info = loadSound(filepath, filename);
        
        handle    = info[0];
        channels  = info[1];
        frequency = info[2];
        
        durationInSeconds = info[3] / (float) frequency;
    }
    
    int[] loadSound(String filepath, String filename) {
        try(InputStream file = Sound.class.getResourceAsStream(filepath + filename)) {
            int[] info = new int[4];
            
            info[0] = alGenBuffers();
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                byte[] data = file.readAllBytes();
                
                ByteBuffer soundBuffer   = MemoryUtil.memAlloc(data.length).put(data).flip();
                IntBuffer channelsBuffer = stack.mallocInt(1);
                IntBuffer sampleBuffer   = stack.mallocInt(1);
                
                ShortBuffer sound = stb_vorbis_decode_memory(soundBuffer, channelsBuffer, sampleBuffer);
                
                info[1] = channelsBuffer.get();
                info[2] = sampleBuffer.get();
                info[3] = sound.capacity() / info[1];
                
                if(info[1] == 1) alBufferData(info[0], AL_FORMAT_MONO16, sound, info[2]);
                else             alBufferData(info[0], AL_FORMAT_STEREO16, sound, info[2]);
                
                MemoryUtil.memFree(soundBuffer);
            }
            
            return info;
            
        } catch(Exception exception) {
            Logger.logWarning("Failed to load sound \"" + filename + "\" a fallback will be used instead", exception);
            
            return new int[] {
                fallback.handle,
                fallback.channels,
                fallback.frequency
            };
        }
    }
    
    void delete() {
        alDeleteBuffers(handle);
    }
    
}