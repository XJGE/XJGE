package org.xjge.core;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class LightingSystem {

    public static final int MAX_LIGHTS = 128;
    private static int activeCount;
    
    private static final Light2[] lights = new Light2[MAX_LIGHTS];
    
    static {
        for(int i = 0; i < MAX_LIGHTS; i++) lights[i] = new Light2();
    }
    
    static void init() {
        
    }
    
    static void render() {
        //upload to GPU
    }
    
    public static Light2 aquire() {
        for(int i = 0; i < MAX_LIGHTS; i++) {
            if(!lights[i].enabled) {
                lights[i].enabled = true;
                return lights[i];
            }
        }
        
        throw new IllegalStateException("Failed to aquire light. The object pool has been exhausted (MAX: " + MAX_LIGHTS + ")");
    }
    
    public static void release(Light2 light) {
        light.reset();
    }
    
}