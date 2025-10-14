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
    
    boolean open() {
        var tempDeviceHandle = alcOpenDevice(name);
        if(tempDeviceHandle == NULL) return false;
        
        var tempContextHandle = alcCreateContext(tempDeviceHandle, (IntBuffer) null);
        
        if(tempContextHandle == NULL) {
            alcCloseDevice(tempDeviceHandle);
            return false;
        }
        
        var tempCapabilities = ALC.createCapabilities(tempDeviceHandle);
        
        try {
            alcMakeContextCurrent(tempContextHandle);
            AL.createCapabilities(tempCapabilities);
            
            var sources = new ArrayList<Integer>();
            
            while(sources.size() < Audio.MAX_SOURCES) {
                int sourceHandle = alGenSources();
                if(alGetError() != AL_NO_ERROR) break;
                sources.add(sourceHandle);
            }
            
            soundSourceLimit = sources.size();
            sources.forEach(AL10::alDeleteSources);
            
            alcMakeContextCurrent(NULL);
            
            if(soundSourceLimit >= Audio.MIN_SOURCES) {
                deviceHandle  = tempDeviceHandle;
                contextHandle = tempContextHandle;
                return true;
            } else {
                alcDestroyContext(tempContextHandle);
                alcCloseDevice(tempDeviceHandle);
                return false;
            }
        } catch(IllegalStateException exception) {
            alcDestroyContext(tempContextHandle);
            alcCloseDevice(tempDeviceHandle);
            return false;
        }
    }
    
    boolean use() {
        try {
            if(deviceHandle == NULL || contextHandle == NULL) {
                throw new IllegalStateException("Device or context handle is null");
            }
            
            alcMakeContextCurrent(contextHandle);
            capabilities = ALC.createCapabilities(deviceHandle);
            AL.createCapabilities(capabilities);
            
            return true;
            
        } catch(IllegalStateException exception) {
            Logger.logWarning("Failed to change current speaker to \"" + name + "\"", exception);
            return false;
        }
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
    
    public int getSoundSourceLimit() {
        return soundSourceLimit;
    }
    
}