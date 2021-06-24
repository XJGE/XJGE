package dev.theskidster.xjge2.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author J Hoffman
 * Created: Jun 20, 2021
 */

public final class Audio {

    private static float soundMasterVolume = 1;
    private static float musicMasterVolume = 1;
    
    static Speaker speaker;
    
    static final Map<String, Integer> soundHandles = new HashMap<>();
    static final Map<String, Integer> songHandles  = new HashMap<>();
    
    static void init() {
        
    }
    
    public static float getSoundMasterVolume() {
        return soundMasterVolume;
    }
    
    public static float getMusicMasterVolume() {
        return musicMasterVolume;
    }
    
    /*
    public static void playSound(Sound sound, vol, etc...) {
        
    }
    */
    
}