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
    
    boolean open(boolean debugModeEnabled) {
        try {
            deviceHandle  = alcOpenDevice(name);
            contextHandle = alcCreateContext(deviceHandle, (IntBuffer) null);
            
            if(deviceHandle == NULL) {
                throw new IllegalStateException("Failed to generate a device handle for speaker \"" + name + "\"");
            } else if(contextHandle == NULL) {
                throw new IllegalStateException("Failed to generate a context handle for speaker \"" + name + "\"");
            }
            
            alcMakeContextCurrent(contextHandle);
            AL.createCapabilities(ALC.createCapabilities(deviceHandle));
            
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
                return true;
            } else {
                throw new IllegalStateException("Speaker \"" + name + "\" does not support the minimum number of sound sources");
            }
        } catch(IllegalStateException exception) {
            if(contextHandle != NULL) alcDestroyContext(contextHandle);
            if(deviceHandle != NULL) alcCloseDevice(deviceHandle);
            
            if(debugModeEnabled) {
                Logger.logWarning("Speaker \"" + name + "\" excluded from list of usable devices", exception);
            }
            
            contextHandle = deviceHandle = NULL;
            return false;
        }
    }
    
    boolean use() {
        try {
            if(deviceHandle == NULL || contextHandle == NULL) {
                throw new IllegalStateException("Speaker \"" + name + "\" has no valid device or " + 
                                                "context handle, it may have been closed by the system");
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