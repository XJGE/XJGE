package dev.theskidster.xjge2.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Jun 22, 2021
 */

public final class Sound {

    public final int handle;
    private int channels;
    private int sampleRate;
    
    public Sound(String filename) {
        handle = alGenBuffers();
        
        try(InputStream file = Sound.class.getResourceAsStream(XJGE.getFilepath() + filename)) {
            loadSound(file);
        } catch(Exception e) {
            Logger.setDomain("audio");
            Logger.logWarning("Failed to load sound \"" + filename + "\"", e);
            Logger.setDomain(null);
            
            loadSound(Sound.class.getResourceAsStream("/dev/theskidster/xjge2/assets/sfx_beep.ogg"));
        }
        
    }
    
    private void loadSound(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer soundBuf   = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer channelsBuf = stack.mallocInt(1);
            IntBuffer sampleBuf   = stack.mallocInt(1);
            
            ShortBuffer sound = stb_vorbis_decode_memory(soundBuf, channelsBuf, sampleBuf);
            
            channels   = channelsBuf.get();
            sampleRate = sampleBuf.get();
            
            if(channels == 1) alBufferData(handle, AL_FORMAT_MONO16, sound, sampleRate);
            else              alBufferData(handle, AL_FORMAT_STEREO16, sound, sampleRate);
            
            //TODO: MemoryUtil.memFree(sound); ?
            MemoryUtil.memFree(soundBuf);
            
        } catch(IOException e) {
            Logger.setDomain("audio");
            Logger.logSevere("Failed to parse sound data.", e);
        }
    }
    
    public int getChannels() {
        return channels;
    }
    
    public int getSampleRate() {
        return sampleRate;
    }
    
    public void freeSound() {
        alDeleteBuffers(handle);
    }
    
}