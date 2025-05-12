package org.xjge.core;

import java.nio.IntBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;

/**
 * Created: Jun 20, 2021
 * <p>
 * Represents a peripheral audio output device such as a speaker, headset, or 
 * headphones.
 * 
 * @author J Hoffman
 * @since  2.0.0
 */
public final class Speaker {

    final int id;
    
    public final long handle;
    private final long context;
    
    public final String name;
    private final ALCCapabilities capabilities;
    
    /**
     * Creates a new audio device.
     * 
     * @param id the unique number used to identify the device in other parts of 
     *           the engine
     * @param name the name of the device as provided by OpenAL
     */
    Speaker(int id, String name) {
        this.id   = id;
        this.name = name;
        
        handle       = alcOpenDevice(name);
        capabilities = ALC.createCapabilities(handle);
        context      = alcCreateContext(handle, (IntBuffer) null);
    }
    
    /**
     * Before an audio device can start playing sounds from 
     * {@link SoundSource} objects, OpenAL must create a context. Using this 
     * method will create a new OpenAL context on the device from which it is 
     * called and subsequently reload any audio data previously allocated into 
     * the new context.
     */
    void setContextCurrent() {
        try {
            alcMakeContextCurrent(context);
            AL.createCapabilities(capabilities);
            Audio.applyContextConfig();
        } catch(IllegalStateException e) {
            Logger.logError("Failed to initialize an OpenAL context on speaker \"" + name + "\"", e);
        }
    }
    
    //TODO: display capabilities?
    //TODO: explore EFX
    
}