package dev.theskidster.xjge2.core;

import java.nio.IntBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;

/**
 * @author J Hoffman
 * Created: Jun 20, 2021
 */

public final class Speaker {

    final int id;
    
    public final long handle;
    private final long context;
    
    public final String name;
    private final ALCCapabilities capabilities;
    
    Speaker(int id, String name) {
        this.id   = id;
        this.name = name;
        
        handle       = alcOpenDevice(name);
        capabilities = ALC.createCapabilities(handle);
        context      = alcCreateContext(handle, (IntBuffer) null);
    }
    
    void setContextCurrent() {
        try {
            alcMakeContextCurrent(context);
            AL.createCapabilities(capabilities);
            Audio.init();
        } catch(IllegalStateException e) {
            Logger.setDomain("audio");
            Logger.logSevere("Failed to initialize an OpenAL context on speaker \"" + name + "\"", e);
        }
    }
    
}