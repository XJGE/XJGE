package dev.theskidster.xjge2.core;

/**
 * @author J Hoffman
 * Created: Jun 20, 2021
 */

public final class Audio {

    private static float soundMasterVolume = 1;
    private static float musicMasterVolume = 1;
    
    static Speaker speaker;
    
    static void init() {
        
    }
    
    public static float getSoundMasterVolume() {
        return soundMasterVolume;
    }
    
    public static float getMusicMasterVolume() {
        return musicMasterVolume;
    }
    
}