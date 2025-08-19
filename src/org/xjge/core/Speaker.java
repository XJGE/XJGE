package org.xjge.core;

import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.AL_NO_ERROR;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetError;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * 
 * @author J Hoffman
 * @since 
 */
public final class Speaker {
    
    final int index;
    private int soundSourceLimit;
    
    long deviceHandle;
    long contextHandle;
    
    public final String name;
    private ALCCapabilities capabilities;
    
    Speaker(int index, String name) {
        this.index = index;
        this.name  = name;
    }
    
    void close() {
        if(contextHandle != NULL) {
            alcDestroyContext(contextHandle);
            contextHandle = NULL;
        }
        
        if(deviceHandle != NULL) {
            alcCloseDevice(deviceHandle);
            deviceHandle = NULL;
        }
    }
    
    boolean open() {
        deviceHandle = alcOpenDevice(name);
        if(deviceHandle == NULL) return false;
        
        capabilities  = ALC.createCapabilities(deviceHandle);
        contextHandle = alcCreateContext(deviceHandle, (IntBuffer) null);
        
        if(capabilities == null || contextHandle == NULL) {
            alcCloseDevice(deviceHandle);
            deviceHandle = NULL;
            return false;
        }
        
        try {
            alcMakeContextCurrent(contextHandle);
            AL.createCapabilities(capabilities);
            
            var sources = new ArrayList<Integer>();
            
            while(sources.size() < Audio.MAX_SOURCES) {
                int sourceHandle = alGenSources();
                if(alGetError() != AL_NO_ERROR) break;
                sources.add(sourceHandle);
            }
            
            soundSourceLimit = sources.size();
            sources.forEach(AL10::alDeleteSources);
            
            alcMakeContextCurrent(NULL);
            
            return soundSourceLimit >= Audio.MIN_SOURCES;
            
        } catch(IllegalStateException exception) {
            return false;
        }
    }
    
    boolean use() {
        try {
            alcMakeContextCurrent(contextHandle);
            AL.createCapabilities(capabilities);
            return true;
        } catch(IllegalStateException exception) {
            Logger.logWarning("Failed to change current speaker to \"" + name + "\"", exception);
            return false;
        }
    }
    
    public int getSoundSourceLimit() {
        return soundSourceLimit;
    }
    
}