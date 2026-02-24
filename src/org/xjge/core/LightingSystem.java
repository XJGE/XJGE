package org.xjge.core;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL31C.*;
import org.xjge.graphics.Texture;

/**
 * 
 * @author J Hoffman
 * @since 4.0.0
 */
public final class LightingSystem {
    
    static boolean showLightSources;
    
    public static final int MAX_LIGHTS = 128;
    private static int ubo;
    private static int activeCount;
    
    private static FloatBuffer buffer;
    private static LightingDebug debug;
    
    private static final Light[] lights = new Light[MAX_LIGHTS];
    
    static {
        for(int i = 0; i < MAX_LIGHTS; i++) lights[i] = new Light();
    }
    
    static void init(Texture engineIcons) {
        debug  = new LightingDebug(engineIcons);
        ubo    = glGenBuffers();
        buffer = BufferUtils.createFloatBuffer(MAX_LIGHTS * 16);
        
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferData(GL_UNIFORM_BUFFER, MAX_LIGHTS * 64L, GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_UNIFORM_BUFFER, 0, ubo);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
    
    static void render() {
        activeCount = 0;
        buffer.clear();
        
        for(int i = 0; i < MAX_LIGHTS; i++) {
            var light = lights[i];
            
            if(!light.enabled) continue;
            
            //position_type
            buffer.put(light.position.x);
            buffer.put(light.position.y);
            buffer.put(light.position.z);
            buffer.put((float) light.type.ordinal());
            
            //direction
            buffer.put(light.direction.x);
            buffer.put(light.direction.y);
            buffer.put(light.direction.z);
            buffer.put(0f);
            
            //color_brightness
            buffer.put(light.color.getRed());
            buffer.put(light.color.getGreen());
            buffer.put(light.color.getBlue());
            buffer.put(light.brightness);
            
            //parameters
            buffer.put(light.range);
            buffer.put(light.falloff);
            buffer.put((float) Math.cos(Math.toRadians(light.innerCone)));
            buffer.put((float) Math.cos(Math.toRadians(light.outerCone)));
            
            activeCount++;
        }
        
        buffer.flip();
        
        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        
        ErrorUtils.checkGLError();
    }
    
    static void debug(Camera camera) {
        if(showLightSources) debug.draw(activeCount, lights, camera);
    }
    
    public static void release(Light light) {
        light.reset();
    }
    
    public static int getActiveCount() {
        return activeCount;
    }
    
    public static Light request() {
        for(int i = 0; i < MAX_LIGHTS; i++) {
            if(!lights[i].enabled) {
                lights[i].enabled = true;
                return lights[i];
            }
        }
        
        throw new IllegalStateException("Failed to aquire light. The object pool has been exhausted (MAX: " + MAX_LIGHTS + ")");
    }
    
}